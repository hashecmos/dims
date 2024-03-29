USE [DIMS]
GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_ADD_ATTACHMENT]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_ADD_ATTACHMENT]
@WfID NVARCHAR(50),
@DocumentID NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;

	BEGIN TRY
		DECLARE @AttID NVARCHAR(50)
		SET @AttID = NEWID()

		DECLARE @ExistID NVARCHAR(50)
		SELECT @ExistID = WFL_ATTACHMENT_ID FROM DIMS_WORKFLOW_ATTACHMENT
		WHERE WFL_ID = @WfID AND WFL_DOCUMENT_ID = @DocumentID
		IF(@ExistID IS NOT NULL)
		THROW 101, 'Attachment Already Exists', 1
	
		DECLARE @Type NVARCHAR(10)
		SET @ExistID = (SELECT TOP(1) WFL_ATTACHMENT_ID FROM DIMS_WORKFLOW_ATTACHMENT
						WHERE WFL_ID = @WfID)
		IF(@ExistID IS NULL)
			SET @Type = 'PRIMARY'
		ELSE
			SET @Type = 'ATTACHMENT'

		INSERT INTO DIMS_WORKFLOW_ATTACHMENT
				(WFL_ATTACHMENT_ID, WFL_ID, WFL_DOCUMENT_ID, WFL_ATTACHMENT_TYPE)
				VALUES
				(@AttID, @WfID, @DocumentID, @Type)
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, NULL, 'ERROR', 'APP';
		THROW 50501, 'Database error while adding attachment', 1;
	END CATCH		
			
END





GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_ADD_ATTACHMENT_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_ADD_ATTACHMENT_WORKITEM]
@WitmID NVARCHAR(50),
@DocumentID NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WfID NVARCHAR(50)
	BEGIN TRY
		SELECT @WfID = WFL_ID FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID

		DECLARE @AttID NVARCHAR(50)
		SET @AttID = NEWID()

		DECLARE @ExistID NVARCHAR(50)
		DECLARE @Type NVARCHAR(10)
		SELECT @ExistID = WFL_ATTACHMENT_ID, @Type = WFL_ATTACHMENT_TYPE FROM DIMS_WORKFLOW_ATTACHMENT
		WHERE WFL_ID = @WfID AND WFL_DOCUMENT_ID = @DocumentID

		IF(@ExistID IS NULL)
		BEGIN
			SET @ExistID = (SELECT TOP(1) WFL_ATTACHMENT_ID FROM DIMS_WORKFLOW_ATTACHMENT
							WHERE WFL_ID = @WfID)
			IF(@ExistID IS NULL)
				SET @Type = 'PRIMARY'
			ELSE
				SET @Type = 'ATTACHMENT'
	
			INSERT INTO DIMS_WORKFLOW_ATTACHMENT
					(WFL_ATTACHMENT_ID, WFL_ID, WFL_DOCUMENT_ID, WFL_ATTACHMENT_TYPE)
					VALUES
					(@AttID, @WfID, @DocumentID, @Type)
		END

		DECLARE @WitmAttID NVARCHAR(50)
		SET @WitmAttID = NEWID()
		DECLARE @ExistID1 NVARCHAR(50)
		SELECT @ExistID1 = WITM_ATTACHMENT_ID FROM DIMS_WORKITEM_ATTACHMENT
		WHERE WFL_ID = @WfID AND WFL_DOCUMENT_ID = @DocumentID AND WFL_WITM_ID = @WitmID
		IF(@ExistID1 IS NULL)
		BEGIN
			INSERT INTO DIMS_WORKITEM_ATTACHMENT
					(WITM_ATTACHMENT_ID, WFL_ID, WFL_WITM_ID, WFL_DOCUMENT_ID, WITM_ATTACHMENT_TYPE)
					VALUES
					(@WitmAttID, @WfID, @WitmID, @DocumentID, @Type)
		END

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, @WitmID, 'ERROR', 'APP';
		THROW 50502, 'Database error while adding attachment to work item', 1;
	END CATCH

END


GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_ADDUSER_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_ADDUSER_WORKITEM]
@InWiID NVARCHAR(50),
@ActionBy NVARCHAR(50),
@Recipient NVARCHAR(50),
@Type NVARCHAR(10),
@Instructions NVARCHAR(255),
@Deadline NVARCHAR(30) = null,
@Reminder NVARCHAR(30) = null,
@SentOnBehalf NVARCHAR(50) = null,
@Status NVARCHAR(20) = null,
@Comments NVARCHAR(1024) = null,
@WitmID NVARCHAR(50) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WfID NVARCHAR(50)
	DECLARE @Parent NVARCHAR(50)
	DECLARE @ExtWitm NVARCHAR(50)
	DECLARE @NodeNo NVARCHAR(1024)
	DECLARE @SeqNo INT = 0
	BEGIN TRY
		SELECT @WfID = WFL_ID, @Parent = WFL_PARENT_WITM , @NodeNo = WFL_WITM_NODE
		FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @InWiID

		IF (@Parent IS NULL)
			BEGIN
				SELECT @ExtWitm = WFL_WITM_ID FROM DIMS_WORKITEM WHERE WFL_ID = @WfID AND WFL_RECIPIENT = @Recipient AND WFL_PARENT_WITM IS NULL AND
					WFL_WITM_SYS_STATUS = 'ACTIVE'
				
				/*SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE LEN(WFL_WITM_NODE) = LEN(@NodeNo) 
					AND WFL_ID = @WfID AND WFL_WITM_TYPE <> 'Reply' AND WFL_PARENT_WITM IS NULL) + 1
				SET @NodeNo = (SELECT FORMAT(@SeqNo, '00#')) */
			END
		ELSE
			BEGIN
				SELECT @ExtWitm = WFL_WITM_ID FROM DIMS_WORKITEM WHERE WFL_ID = @WfID AND WFL_RECIPIENT = @Recipient AND WFL_PARENT_WITM = @Parent AND
					WFL_WITM_SYS_STATUS = 'ACTIVE'
				/*
				SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE LEN(WFL_WITM_NODE) = LEN(@NodeNo) 
					AND WFL_ID = @WfID AND WFL_WITM_TYPE <> 'Reply' AND WFL_PARENT_WITM = @Parent) + 1

				IF LEN(@NodeNo) > 3
					SET @NodeNo = (SELECT LEFT(@NodeNo, LEN(@NodeNo) - 3))
				ELSE
					SET @NodeNo = ''
							
				SET @NodeNo = (SELECT CONCAT(@NodeNo, FORMAT(@SeqNo, '00#'))) */
			END
		IF @ExtWitm IS NOT NULL
			THROW 50102, 'The recipient already exists', 1;

		SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_ID = @WfID AND
			LEFT(WFL_WITM_NODE, LEN(WFL_WITM_NODE)-3) = LEFT(@NodeNo, LEN(@NodeNo)-3) AND 
			LEN(@NodeNo) = LEN(WFL_WITM_NODE)
			AND WFL_WITM_TYPE <> 'Reply') + 1
		
		IF LEN(@NodeNo) > 3
			SET @NodeNo = (SELECT LEFT(@NodeNo, LEN(@NodeNo) - 3))
		ELSE
			SET @NodeNo = ''
							
		SET @NodeNo = (SELECT CONCAT(@NodeNo, FORMAT(@SeqNo, '00#')))

		SET @WitmID = NEWID()

		DECLARE @DeadlineDate DATETIME
		DECLARE @ReminderDate DATETIME

		IF (@Deadline IS NULL) OR (LEN(@Deadline) <= 0)
			SET @DeadlineDate = DATEADD(day,5,GETDATE())
		ELSE
			SET @DeadlineDate = convert(datetime, @Deadline,103)

		if (@Reminder IS NULL) OR (LEN(@Reminder) <= 0)
			SET @ReminderDate = DATEADD(day, -1, @DeadlineDate)
		ELSE
			SET @ReminderDate = convert(datetime, @Reminder,103)

		IF(@SentOnBehalf IS NULL)
			SET @SentOnBehalf = @ActionBy

		DECLARE @StepNo INT = 1
		DECLARE @RootSender NVARCHAR(50) = @SentOnBehalf
		IF(@Parent IS NOT NULL)
		BEGIN
			SET @StepNo = (SELECT TOP(1) WFL_STEP_NO FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent) + 1
			SET @RootSender = (SELECT TOP(1) WFL_WITM_ROOT_SENDER FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent)
		END 

		DECLARE @SenderDept INT
		DECLARE @SenderDiv INT
		DECLARE @ReceiverDept INT
		DECLARE @ReceiverDiv INT

		SET @SenderDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @SentOnBehalf)

		SET @ReceiverDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		SET @SenderDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @SentOnBehalf)

		SET @ReceiverDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		IF (@Status IS NULL)
			SET @Status = 'New'

		if(@Comments IS NULL)
			SET @Comments = ''

		INSERT INTO DIMS_WORKITEM
				(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
				WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
				WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
				WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
				WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
				WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
				VALUES
				(@witmID, @WfID, @SentOnBehalf, @Recipient, @Type, @StepNo, 
				@Parent, @Instructions, GETDATE(), @DeadlineDate,
				@ReminderDate, @Status, @RootSender, @SenderDept,
				@SenderDiv, @ReceiverDept, @ReceiverDiv, @Status,
				@ActionBy, @SentOnBehalf, @Comments, 'ACTIVE', @SeqNo, @NodeNo)

		DECLARE @PrimaryDocID NVARCHAR(50)
		SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WfID)

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WfID, @ActionBy, GETDATE(), @WitmID, @Comments,@PrimaryDocID,@Status)

		

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, @InWiID, 'ERROR', 'APP';
		THROW 50503, 'Database error while adding user to work item', 1;
	END CATCH

		

	
END





GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_ARCHIVE_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_ARCHIVE_WORKITEM]
@WitmID NVARCHAR(50),
@actionBy NVARCHAR(50),
@actionOnBehalf NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WflID NVARCHAR(50)
	DECLARE @wiStatus NVARCHAR(10)
	BEGIN TRY
		SELECT @WflID = WFL_ID FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID

		IF @actionOnBehalf IS NULL
			SET @actionOnBehalf = @actionBy

		UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS = 'Archive', WFL_WITM_ACTION = 'Archive',
			WFL_WITM_ACTION_BY = @actionBy, WFL_WITM_ACTION_ONBEHALF = @actionOnBehalf
			 WHERE WFL_WITM_ID = @WitmID

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WflID, @actionBy, GETDATE(), @WitmID, 'Archived Item', NULL, 'Archive')

		

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 50514, 'Database error while archiving work item', 1;
	END CATCH
END





GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_COMPLETE_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_COMPLETE_WORKITEM]
@WitmID NVARCHAR(50),
@actionBy NVARCHAR(50),
@actionOnBehalf NVARCHAR(50) = null
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WflID NVARCHAR(50)
	BEGIN TRY
		IF(@actionOnBehalf IS NULL)
			SET @actionOnBehalf = @actionBy

		SELECT @WflID = WFL_ID FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID

		UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS='Complete', WFL_WITM_ACTION='Complete',
				WFL_WITM_ACTION_BY = @actionBy,  WFL_WITM_ACTION_ONBEHALF = @actionOnBehalf, 
				WFL_WITM_SYS_STATUS = 'INACTIVE'
				WHERE WFL_ID = @WflID

		UPDATE DIMS_WORKFLOW SET WFL_STATUS = 'INACTIVE', WFL_COMPLETEDBY = @actionBy,
				WFL_COMPLETEDONBEHALF = @actionOnBehalf, WFL_ENDDATE = GETDATE() 
				WHERE WFL_ID = @WflID

		DECLARE @PrimaryDocID NVARCHAR(50)
		SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WflID)

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WflID, @actionBy, GETDATE(), @WitmID, 'Completed Workflow',@PrimaryDocID,'Complete')

		

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 50504, 'Database error while completing work item', 1;
	END CATCH
			
END





GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_CREATE_WORKFLOW]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_CREATE_WORKFLOW]
@Subject NVARCHAR(200),
@Deadline NVARCHAR(30) = NULL,
@Reminder NVARCHAR(30) = NULL,
@Priority INT = 1,
@LaunchedBY NVARCHAR(50),
@LaunchedOnBehalf NVARCHAR(50) = NULL,
@PrimaryDocID NVARCHAR(50),
@WfID NVARCHAR(50) OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	BEGIN TRY
		SET @WfID = NEWID()

		DECLARE @DeadlineDate DATETIME
		DECLARE @ReminderDate DATETIME

		IF (@Deadline IS NULL) OR (LEN(@Deadline) <= 0)
			SET @DeadlineDate = DATEADD(day,5,GETDATE())
		ELSE
			SET @DeadlineDate = DATEADD(minute,59,DATEADD(hour,23,convert(datetime,  @Deadline,103)))

		if (@Reminder IS NULL) OR (LEN(@Reminder) <= 0)
			SET @ReminderDate = DATEADD(day, -1, @DeadlineDate)
		ELSE
			SET @ReminderDate = DATEADD(minute,59,DATEADD(hour,23,convert(datetime,  @Reminder,103)))

		IF(@LaunchedOnBehalf IS NULL) OR (LEN(@LaunchedOnBehalf) <= 0)
			SET @LaunchedOnBehalf = @LaunchedBY

		DECLARE @WfName NVARCHAR(50)
		IF(LEN(@Subject) > 35)
			SET @WfName = 'Correspondence:' + LEFT(@Subject, 35)
		ELSE
			SET @WfName = 'Correspondence:' + @Subject

		IF (@Priority IS NULL) OR (@Priority < = 0) OR (@Priority > 3)
			SET @Priority = 1

		INSERT INTO DIMS_WORKFLOW
				(WFL_ID, WFL_NAME, WFL_SUBJECT, WFL_DEADLINE, WFL_REMINDER_DATE, WFL_PRIORITY,WFL_LAUNCHEDBY,WFL_LAUNCHEDONBEHALF,WFL_BEGINDATE,WFL_STATUS, WFL_PRIMARY_DOCUMENT)
				VALUES
				(@WfID, @WfName, @Subject, @DeadlineDate, @ReminderDate, @Priority, @LaunchedBY,@LaunchedOnBehalf,GETDATE(),'ACTIVE', @PrimaryDocID)

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, NULL, 'ERROR', 'APP';
		THROW 50506, 'Database error while creating workflow', 1;
	END CATCH

END






GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_CREATE_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_CREATE_WORKITEM]
@WfID NVARCHAR(50),
@Sender NVARCHAR(50),
@Recipient NVARCHAR(50),
@Type NVARCHAR(10),
@Parent NVARCHAR(50),
@Instructions NVARCHAR(255),
@Deadline NVARCHAR(30) = null,
@Reminder NVARCHAR(30) = null,
@SentOnBehalf NVARCHAR(50) = null,
@Status NVARCHAR(20) = null,
@Comments NVARCHAR(1024) = null,
@ActionBy NVARCHAR(50),
@WitmID NVARCHAR(50) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;
	BEGIN TRY
		SET @WitmID = NEWID()

		DECLARE @DeadlineDate DATETIME
		DECLARE @ReminderDate DATETIME

		IF (@Deadline IS NULL) OR (LEN(@Deadline) <= 0)
			SET @DeadlineDate = DATEADD(day,5,GETDATE())
		ELSE
			SET @DeadlineDate = DATEADD(minute,59,DATEADD(hour,23,convert(datetime,  @Deadline,103)))

		if (@Reminder IS NULL) OR (LEN(@Reminder) <= 0)
			SET @ReminderDate = DATEADD(day, -1, @DeadlineDate)
		ELSE
			SET @ReminderDate = DATEADD(minute,59,DATEADD(hour,23,convert(datetime,  @Reminder,103)))

		IF(@SentOnBehalf IS NULL)
			SET @SentOnBehalf = @Sender

		DECLARE @StepNo INT = 1
		DECLARE @RootSender NVARCHAR(50) = @Sender
		DECLARE @SeqNo INT = 0
		DECLARE @NodeNo NVARCHAR(1024)
		IF(@Parent IS NOT NULL)
			BEGIN
				SET @StepNo = (SELECT TOP(1) WFL_STEP_NO FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent) + 1
				SET @RootSender = (SELECT TOP(1) WFL_WITM_ROOT_SENDER FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent)
				SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent) + 1
				SET @NodeNo = (SELECT TOP(1) WFL_WITM_NODE FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent)
				SET @NodeNo = (SELECT CONCAT(@NodeNo, FORMAT(@SeqNo, '00#')))
			END 
		ELSE
			BEGIN
				SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM IS NULL AND WFL_ID = @WfID) + 1
				SET @NodeNo = (SELECT FORMAT(@SeqNo, '00#'))
			END

		DECLARE @SenderDept INT
		DECLARE @SenderDiv INT
		DECLARE @ReceiverDept INT
		DECLARE @ReceiverDiv INT

		SET @SenderDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Sender)

		SET @ReceiverDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		SET @SenderDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Sender)

		SET @ReceiverDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		IF (@Status IS NULL)
			SET @Status = 'New'
		if(@Comments IS NULL)
			SET @Comments = 'Launched'

		INSERT INTO DIMS_WORKITEM
				(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
				WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
				WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
				WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
				WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
				WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
				VALUES
				(@witmID, @WfID, @Sender, @Recipient, @Type, @StepNo, 
				@Parent, @Instructions, GETDATE(), @DeadlineDate,
				@ReminderDate, @Status, @RootSender, @SenderDept,
				@SenderDiv, @ReceiverDept, @ReceiverDiv, @Status,
				@ActionBy, @SentOnBehalf, @Comments, 'ACTIVE', @SeqNo, @NodeNo)

		DECLARE @PrimaryDocID NVARCHAR(50)
		SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WfID)

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WfID, @ActionBy, GETDATE(), @WitmID, @Comments,@PrimaryDocID,@Status)

				
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, @WitmID, 'ERROR', 'APP';
		THROW 50505, 'Database error while creating work item', 1;
	END CATCH


END





GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_DONE_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_DONE_WORKITEM]
@WitmID NVARCHAR(50),
@actionBy NVARCHAR(50),
@actionComments NVARCHAR(50),
@actionOnBehalf NVARCHAR(50) = null,
@NewWitmID NVARCHAR(50) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WflID NVARCHAR(50)
	DECLARE @Recipient NVARCHAR(50)
	
	DECLARE @DeadlineDate DATETIME
	DECLARE @ReminderDate DATETIME
	DECLARE @RootSender NVARCHAR(50)
	DECLARE @StepNo INT = 1
	DECLARE @ParentWitmID NVARCHAR(50)
	DECLARE @NodeNo NVARCHAR(1024)
	DECLARE @SeqNo INT = 1
	DECLARE @type NVARCHAR(20)

	BEGIN TRY
		IF(@actionOnBehalf IS NULL)
		SET @actionOnBehalf = @actionBy

	SELECT @WflID = WFL_ID, @DeadlineDate = WFL_WITM_DEADLINE, @Recipient = WFL_SENDER,
			@ReminderDate = WFL_WITM_REMINDER_DATE, @RootSender = WFL_WITM_ROOT_SENDER,
			@StepNo = WFL_STEP_NO, @NodeNo = WFL_WITM_NODE, @type = WFL_WITM_TYPE  
			FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID

	UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS='Done', WFL_WITM_ACTION='Done',
			WFL_WITM_ACTION_BY = @actionBy, WFL_WITM_ACTION_COMMENT = @actionComments,
			WFL_WITM_ACTION_ONBEHALF = @actionOnBehalf, WFL_WITM_SYS_STATUS = 'INACTIVE'
			WHERE WFL_WITM_ID = @WitmID

	DECLARE @wiRecipient NVARCHAR(50)
	DECLARE @oldNodeNo NVARCHAR(MAX)
	SET @wiRecipient = @Recipient
	SET @oldNodeNo = @NodeNo
	IF LEN(@NodeNo) > 3
		BEGIN
			SET @NodeNo = (SELECT LEFT(@NodeNo, LEN(@NodeNo) - 3))
			IF(SELECT COUNT(*) FROM DBO.DIMS_WORKITEM WHERE WFL_ID =  @WflID AND WFL_WITM_NODE = @oldNodeNo) > 1
			BEGIN			
				SET @Recipient = (SELECT TOP(1) WFL_RECIPIENT FROM DBO.DIMS_WORKITEM WHERE WFL_ID =  @WflID AND WFL_WITM_NODE = @NodeNo AND WFL_WITM_TYPE <> 'Reply' AND WFL_WITM_STATUS <> 'Done')
				IF(@Recipient IS NULL)
					SET @Recipient = @wiRecipient
			END
			ELSE
				SET @Recipient = @wiRecipient
		END
	ELSE
	BEGIN
		SET @Recipient = @RootSender
		SET @NodeNo = ''
	END

	IF(@type <> 'Reply')
		SET @Recipient = @wiRecipient

	SET @NewWitmID = NEWID()
	
	IF(@StepNo IS NOT NULL)
		SET @StepNo = @StepNo + 1
	if(@actionComments IS NULL)
		SET @actionComments = ''

	DECLARE @SenderDept INT
	DECLARE @SenderDiv INT
	DECLARE @ReceiverDept INT
	DECLARE @ReceiverDiv INT

	SET @SenderDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
						WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @actionOnBehalf)

	SET @ReceiverDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
						WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

	SET @SenderDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
						WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @actionOnBehalf)

	SET @ReceiverDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
						WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)
	EXEC dbo.APP_DIMS_INACTIVATE_CHILD_WORKITEMS @WitmID
	INSERT INTO DIMS_WORKITEM
			(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
			WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
			WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
			WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
			WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
			WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
			VALUES
			(@NewwitmID, @WflID, @actionOnBehalf, @Recipient, 'Reply', @StepNo, 
			@WitmID, 'Return', GETDATE(), @DeadlineDate,
			@ReminderDate, 'New', @RootSender, @SenderDept,
			@SenderDiv, @ReceiverDept, @ReceiverDiv, 'Done',
			@actionBy, @actionOnBehalf, @actionComments, 'ACTIVE', @SeqNo, @NodeNo)

	DECLARE @PrimaryDocID NVARCHAR(50)
	SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WflID)

	INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
			WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
			VALUES (NEWID(), @WflID, @actionBy, GETDATE(), @NewwitmID, @actionComments,@PrimaryDocID,'Done')



	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 50507, 'Database error while performing done on work item', 1;
	END CATCH
		
END





GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_FORWARD_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_FORWARD_WORKITEM]
@WitmID NVARCHAR(50),
@actionBy NVARCHAR(50),
@actionComments NVARCHAR(50),
@actionOnBehalf NVARCHAR(50) = null,
@Recipient NVARCHAR(50),
@Type NVARCHAR(10),
@Instructions NVARCHAR(255),
@Deadline NVARCHAR(30) = null,
@Reminder NVARCHAR(30) = null,
@Iterator INT = 0,
@NewWitmID NVARCHAR(50) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WflID NVARCHAR(50)
	DECLARE @DeadlineDate DATETIME
	DECLARE @ReminderDate DATETIME
	DECLARE @RootSender NVARCHAR(50)
	DECLARE @StepNo INT = 1
	DECLARE @NodeNo NVARCHAR(1024)
	DECLARE @SeqNo INT = 0
	DECLARE @PType NVARCHAR(20)
	BEGIN TRY
		IF(@actionOnBehalf IS NULL)
			SET @actionOnBehalf = @actionBy

		SELECT @WflID = WFL_ID, @RootSender = WFL_WITM_ROOT_SENDER, @StepNo = WFL_STEP_NO, @NodeNo = WFL_WITM_NODE,
				@PType = WFL_WITM_TYPE FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID
		SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_ID = @WflID AND
			LEFT(WFL_WITM_NODE, LEN(WFL_WITM_NODE) - 3) = @NodeNo AND (LEN(@NodeNo) + 3) = LEN(WFL_WITM_NODE)
			AND WFL_WITM_TYPE <> 'Reply') + 1
		
		/*IF(@PType <> 'Reply')
			SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @WitmID) + 1
		ELSE
			SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @WitmID
			AND LEN(WFL_WITM_NODE) = LEN(@NodeNo) AND WFL_ID = @WflID) + 1 */

		SET @NodeNo = (SELECT CONCAT(@NodeNo, FORMAT(@SeqNo, '00#')))

		IF @Iterator <= 0
			UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS='Forward', WFL_WITM_ACTION='Forward',
				WFL_WITM_ACTION_BY = @actionBy, 
				WFL_WITM_ACTION_ONBEHALF = @actionOnBehalf, WFL_WITM_SYS_STATUS = 'INACTIVE'
				WHERE WFL_WITM_ID = @WitmID

		SET @NewWitmID = NEWID()
	
		IF (@Deadline IS NULL) OR (LEN(@Deadline) <= 0)
			SET @DeadlineDate = DATEADD(day,5,GETDATE())
		ELSE
			SET @DeadlineDate = convert(datetime, @Deadline,103)

		if (@Reminder IS NULL) OR (LEN(@Reminder) <= 0)
			SET @ReminderDate = DATEADD(day, -1, @DeadlineDate)
		ELSE
			SET @ReminderDate = convert(datetime, @Reminder,103)

		IF(@StepNo IS NOT NULL)
			SET @StepNo = @StepNo + 1
		IF(@actionComments IS NULL)
			SET @actionComments = ''

		DECLARE @SenderDept INT
		DECLARE @SenderDiv INT
		DECLARE @ReceiverDept INT
		DECLARE @ReceiverDiv INT

		SET @SenderDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @actionOnBehalf)

		SET @ReceiverDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		SET @SenderDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @actionOnBehalf)

		SET @ReceiverDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		INSERT INTO DIMS_WORKITEM
				(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
				WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
				WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
				WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
				WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
				WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
				VALUES
				(@NewwitmID, @WflID, @actionOnBehalf, @Recipient, @Type, @StepNo, 
				@WitmID, @Instructions, GETDATE(), @DeadlineDate,
				@ReminderDate, 'New', @RootSender, @SenderDept,
				@SenderDiv, @ReceiverDept, @ReceiverDiv, 'Forward',
				@actionBy, @actionOnBehalf, @actionComments, 'ACTIVE', @SeqNo, @NodeNo)

		DECLARE @PrimaryDocID NVARCHAR(50)
		SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WflID)

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WflID, @actionBy, GETDATE(), @NewwitmID, @actionComments,@PrimaryDocID,'Forward')

				DECLARE @WFL_PRIORITY1 AS INT
				SELECT       @WFL_PRIORITY1=[WFL_PRIORITY]        FROM [DIMS].[dbo].[DIMS_WORKFLOW] WHERE WFL_ID=@WflID

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 50508, 'Database error while forwarding work item', 1;
	END CATCH

END





GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_INACTIVATE_CHILD_WORKITEMS]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_INACTIVATE_CHILD_WORKITEMS]
@WitmID NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @NodeNo NVARCHAR(200)
	DECLARE @WflID NVARCHAR(50)
	BEGIN TRY
		SELECT @NodeNo = WFL_WITM_NODE, @WflID = WFL_ID FROM dbo.DIMS_WORKITEM 
			WHERE WFL_WITM_ID = @WitmID

		DECLARE @orWitmID NVARCHAR(50)
		SET @orWitmID = (SELECT TOP(1) WFL_WITM_ID FROm DIMS_WORKITEM 
			WHERE WFL_WITM_NODE = @NodeNo AND WFL_WITM_ID != @WitmID AND WFL_ID = @WflID)
		
		DECLARE db_cursor CURSOR FOR  
		SELECT WFL_WITM_ID 
		FROM dbo.DIMS_WORKITEM 
		WHERE WFL_ID = @WflID 

		DECLARE @childWi NVARCHAR(50)
		OPEN db_cursor   
		FETCH NEXT FROM db_cursor INTO @childWi   

		WHILE @@FETCH_STATUS = 0   
		BEGIN   
			IF(dbo.ISPARENTOF(@orWitmID, @childWi) > 0)
				UPDATE DIMS_WORKITEM SET WFL_WITM_SYS_STATUS = 'INACTIVE'
				WHERE WFL_WITM_ID = @childWi

			FETCH NEXT FROM db_cursor INTO @childWi  
		END   

		CLOSE db_cursor   
		DEALLOCATE db_cursor
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 50509, 'Database error while inactivating child work item', 1;
	END CATCH
		
END



GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_READ_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_READ_WORKITEM]
@WitmID NVARCHAR(50),
@userLogin NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @Recipient NVARCHAR(50)
	DECLARE @WflID NVARCHAR(50)
	DECLARE @wiStatus NVARCHAR(10)

	BEGIN TRY
		SELECT @wiStatus = WFL_WITM_STATUS, @Recipient = WFL_RECIPIENT, @WflID = WFL_ID FROM DIMS_WORKITEM
		WHERE WFL_WITM_ID = @WitmID

		IF (@userLogin IS NULL)
			SET @userLogin = @Recipient

		IF(@wiStatus = 'New')
		BEGIN
			UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS = 'Read', WFL_WITM_ACTION = 'Read',
				WFL_WITM_ACTION_BY = @userLogin, WFL_WITM_ACTION_ONBEHALF = @Recipient WHERE WFL_WITM_ID = @WitmID

			INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
					WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
					VALUES (NEWID(), @WflID, @userLogin, GETDATE(), @WitmID, 'Read', NULL, 'Read')

			BEGIN /* Sync with Old DIMS */
		DECLARE @syncInputs NVARCHAR(MAX) = ''
		DECLARE @syncError NVARCHAR(MAX) = ''
		BEGIN TRY
			SET @syncInputs = 'WitemID: ' + ISNULL(@WitmID, 'NULL') + ' WflID: ' + ISNULL(@wflID, 'NULL') + 
				' Action By: ' + ISNULL(@userLogin, 'NULL') + ' Param 2: 1';

		Exec DIMS_DB.dbo.Proc_ChangeStatus_WI_OLDDIMS @WitmID,1,@userLogin

		END TRY
		BEGIN CATCH
			SELECT @syncError = ERROR_NUMBER() + ' : ' + ERROR_MESSAGE(); 
			INSERT INTO SYNC_ERROR(WorkflowID, WorkitemID, Input, Message) 
			VALUES (@WflID, @WitmID, @syncInputs, @syncError);
		END CATCH

	END 		/* End Sync with Old DIMS */

		END
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 50510, 'Database error while reading work item', 1;
	END CATCH
END






GO
/****** Object:  StoredProcedure [dbo].[APP_DIMS_REASSIGN_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[APP_DIMS_REASSIGN_WORKITEM]
@InWiID NVARCHAR(50),
@Recipient NVARCHAR(50),
@actionBy NVARCHAR(50),
@actionOnBehalf NVARCHAR(50) = null,
@actionComments NVARCHAR(255),
@originalRecipient NVARCHAR(50),
@WitmID NVARCHAR(50) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WfID NVARCHAR(50)
	
	DECLARE @ExtWitm NVARCHAR(50)
	DECLARE @DeadlineDate DATETIME
	DECLARE @ReminderDate DATETIME
	DECLARE @StepNo INT
	DECLARE @RootSender NVARCHAR(50)
	DECLARE @Type NVARCHAR(10)
	DECLARE @Instructions NVARCHAR(255)
	DECLARE @NodeNo NVARCHAR(1024)
	DECLARE @origWitmID NVARCHAR(50)
	DECLARE @SeqNo INT = 0
	DECLARE @NewNode NVARCHAR(1024)
	
	DECLARE @originalSender NVARCHAR(50)
	DECLARE @OriginalParent NVARCHAR(50)
	DECLARE @wiRecipient NVARCHAR(50)
	BEGIN TRY
		SELECT @WfID = WFL_ID, @wiRecipient = WFL_RECIPIENT FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @InWiID
		IF(@wiRecipient != @originalRecipient)
		BEGIN
			SET @origWitmID = (SELECT TOP(1) WFL_WITM_ID FROM DIMS_WORKITEM WHERE WFL_ID = @WfID AND
						WFL_RECIPIENT = @originalRecipient AND WFL_WITM_TYPE = 'to' AND WFL_WITM_SYS_STATUS = 'ACTIVE')
			IF(@origWitmID IS NULL) 
				SET @origWitmID = @InWiID
		END
		ELSE
			SET @origWitmID = @InWiID

		SELECT  @DeadlineDate = WFL_WITM_DEADLINE, @ReminderDate = WFL_WITM_REMINDER_DATE, 
				@StepNo = WFL_STEP_NO, @RootSender = WFL_WITM_ROOT_SENDER, @Type = WFL_WITM_TYPE, 
				@Instructions = WFL_INSTRUCTIONS, @NodeNo = WFL_WITM_NODE, @SeqNo = WFL_WITM_SEQNO,
				@NewNode = WFL_WITM_NODE, @OriginalParent = WFL_PARENT_WITM, @originalSender = WFL_SENDER 
		FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @origWitmID

		UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS='Reassign', WFL_WITM_ACTION='Reassign',
				WFL_WITM_ACTION_BY = @actionBy,  WFL_WITM_ACTION_ONBEHALF = @actionOnBehalf, 
				WFL_WITM_ACTION_COMMENT = @actionComments, WFL_WITM_SYS_STATUS = 'INACTIVE',
				WFL_WITM_SEQNO = 999, WFL_WITM_NODE = '999'
				WHERE WFL_WITM_ID = @origWitmID

		SET @WitmID = NEWID()

		IF(@actionOnBehalf IS NULL)
			SET @actionOnBehalf = @actionBy

		DECLARE @SenderDept INT
		DECLARE @SenderDiv INT
		DECLARE @ReceiverDept INT
		DECLARE @ReceiverDiv INT

		SET @SenderDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @originalSender)

		SET @ReceiverDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		SET @SenderDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @originalSender)

		SET @ReceiverDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		INSERT INTO DIMS_WORKITEM
				(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
				WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
				WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
				WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
				WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
				WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
				VALUES
				(@witmID, @WfID, @originalSender, @Recipient, @Type, @StepNo, 
				@OriginalParent, @Instructions, GETDATE(), @DeadlineDate,
				@ReminderDate, 'New', @RootSender, @SenderDept,
				@SenderDiv, @ReceiverDept, @ReceiverDiv, 'Reassign',
				@actionBy, @actionOnBehalf, @actionComments, 'ACTIVE',
				@SeqNo, @NewNode)

		DECLARE @PrimaryDocID NVARCHAR(50)
		SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WfID)

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WfID, @actionBy, GETDATE(), @WitmID, 'Reassigned',@PrimaryDocID,'Reassign')


	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, @WitmID, 'ERROR', 'APP';
		THROW 50511, 'Database error while reassigning work item', 1;
	END CATCH
END


GO
/****** Object:  StoredProcedure [dbo].[CREATE_USER_LIST]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author, Atla Man Mohan Varma>
-- Create date: <Create Date, 20 March 2017>
-- Description:	<Description, This procedure will launch the workflow and store the workitem,history and attachment details>
-- =============================================
CREATE PROCEDURE [dbo].[CREATE_USER_LIST]
	
	@loginUser VARCHAR(50),
	@listName VARCHAR(50),
	@listType VARCHAR(10),
	@listMember VARCHAR(50),
	@identifier int OUTPUT
	
AS
	DECLARE @sqlstmt VARCHAR(4000);
	
	BEGIN TRY
		
		INSERT INTO DIMS_USER_LIST
		(WFL_LIST_NAME,WFL_USER_LOGIN,WFL_LIST_TYPE) 
		VALUES
		(@listName,@loginUser,@listType)
		
		SET @identifier = SCOPE_IDENTITY() 
		
		INSERT INTO DIMS_USER_LIST_DETAIL
		(WFL_LIST_ID,WFL_USER_LOGIN) 
		VALUES
		(@identifier,@loginUser)
		
		RETURN
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR NULL, NULL, 'ERROR', 'APP';
		THROW 512, 'Database error while creating user list', 1;
	END CATCH






GO
/****** Object:  StoredProcedure [dbo].[DIMS_LOG_ERROR]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[DIMS_LOG_ERROR]
@WfID NVARCHAR(50),
@WitmID NVARCHAR(50),
@Type NVARCHAR(20),
@Source NVARCHAR(20)
AS
BEGIN
	SET NOCOUNT ON;

	BEGIN TRY

		INSERT INTO dbo.DIMS_ERRORS(WFLID, WITEMID, MESSAGE, TYPE, SPROCEDURE, SLINENO, ERRORNUM, SOURCE)
		VALUES(@wfID, @WitmID, ERROR_MESSAGE(), @Type, ERROR_PROCEDURE(), ERROR_LINE(), ERROR_NUMBER(), @Source)
	END TRY
	BEGIN CATCH
		PRINT 'Done'
	END CATCH		
			
END





GO
/****** Object:  StoredProcedure [dbo].[DIMS_NOTIFICATION]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Bashir Rahmatulla Shiekh	
-- Create date: 10 Oct 2017	
-- Description:	Send email notifcation
-- =============================================
CREATE PROCEDURE [dbo].[DIMS_NOTIFICATION] 

	 @WFID NVARCHAR(50),
	 @NewWitmID NVARCHAR(50),
	 @WitmID NVARCHAR(50)

AS
BEGIN

	DECLARE @WfName NVARCHAR(50)
	DECLARE @Subject NVARCHAR(200)
	Set @WFName=(Select WFL_Name from DIMS_WORKFLOW where WFL_ID=@WfID)
	Set @Subject=(Select WFL_SUBJECT from DIMS_WORKFLOW where WFL_ID=@WfID)

	DECLARE @SentOnBehalf NVARCHAR(50)
	Set @SentOnBehalf = (Select WFL_Sender from DIMS_Workitem where Wfl_Witm_ID=@NewWitmID)

	DECLARE @SenderName NVARCHAR(200)
	Set @SenderName=(Select ECM_USER_NAME from ECM_EMPLOYEE where ECM_USER_LOGIN=@SentOnBehalf)

	DECLARE @Recipient NVARCHAR(50)
    Set @Recipient = (Select WFL_RECIPIENT from DIMS_WORKITEM where WFL_WITM_ID=@NewWitmID)

	DECLARE @ReceiverDept int
	Set @ReceiverDept=(Select ECM_DEPT_CODE from ECM_EMPLOYEE where ECM_USER_LOGIN=@Recipient)

	DECLARE @Instructions NVARCHAR(255)
	Set @Instructions = (Select WFL_INSTRUCTIONS from DIMS_WORKITEM where WFL_WITM_ID=@NewWitmID)

	DECLARE @msg NVARCHAR(MAX)
	DECLARE @Status NVARCHAR(10)
	Set @Status = (Select Wfl_Witm_Status from DIMS_WORKITEM where WFL_WITM_ID=@WitmID)
	IF (@Status<>'Done')
	Set @msg = '<font style="font-size:12pt;padding:15px;font-family:Candara,Century Gothic,Calibri,Verdana"><b>'+ @Instructions +'</b><br/>'+
               '<h3> You have received a New Work Item </h3>'
	Else
	Set @msg = '<font style="font-size:12pt;padding:15px;font-family:Candara,Century Gothic,Calibri,Verdana"><br/>'+
               '<h3> WorkItem is Done </h3>'


	DECLARE @Attachment NVARCHAR(MAX) = ''
	DECLARE @DocID NVARCHAR(50)
	DECLARE @DocumentCSID NVARCHAR(50)
	DECLARE @MessageOutput VARCHAR(100)
	

    DECLARE Attachment_Cursor CURSOR FOR 
    SELECT WFL_DOCUMENT_ID FROM DIMS.[dbo].[DIMS_WORKITEM_ATTACHMENT] where WFL_WITM_ID=@NewWitmID

    OPEN Attachment_Cursor 

    FETCH NEXT FROM Attachment_Cursor INTO
    @DocID

    WHILE @@FETCH_STATUS = 0
    BEGIN
	
	Select  @DocumentCSID=u68_documentid from knpcobj.dbo.DocVersion where object_id=@DocID 
	
	Set @Attachment = @Attachment + '<LI>'+ @DocumentCSID + '&nbsp&nbsp&nbsp&nbsp&nbsp<a href=http://vmshofdims844.main.knpcdom.net:9080/WorkplaceXT/iviewpro/WcmJavaViewer.jsp?id='+ @DocID +'&objectStoreName=DIMS&objectType=document>View</a>' +'</LI>'

	Print(@Attachment)


    RAISERROR(@MessageOutput,0,1) WITH NOWAIT


    FETCH NEXT FROM Attachment_Cursor INTO
    @DocID


    
	END
    CLOSE Attachment_Cursor
    DEALLOCATE Attachment_Cursor

	DECLARE @URL NVARCHAR(200)
	if (@ReceiverDept = 196100)
	SET @URL = 'https://dims2.knpc.net:9443/DIMS'
	else 
	SET @URL = 'https://dims.knpc.net/bf001.aspx'

	DECLARE @recp varchar(max)
	DECLARE @sub nvarchar(200)
	DECLARE @message nvarchar(max)


	SELECT @recp = ECM_USER_EMAIL from ecm_employee where ECM_USER_LOGIN= @Recipient;

	Set @sub='WF From: ' + @SenderName + ', Subj.: ' + @Subject
	Set @message= @msg +
' Work Item Information:<UL>'+
' <LI><font style="color:gray"> Sender Name :</font> ' + @SenderName +' .</LI>'+
' <LI> <font style="color:gray">WorkFlow Name & Subject :</font> ' + @WFName + '.</LI>'+
' <LI> <font style="color:gray">Attachments :</font> ' +'<OL>'+ @Attachment + '</OL></LI>'+
'</UL><font/>'+
'<font style="color:gray;font-size:smaller">'+
'<br/>WorkFlow Application Link :<a href='+ @URL +'>Click here</a> .<br/>'+
'------------------------------------------------------------------<br/>THIS IS A NOTIFICATION MESSAGE ONLY.<br/>YOU DO NOT NEED TO RESEND YOUR MESSAGE.<br/>------------------------------------------------------------------</font>'
	EXEC msdb.dbo.sp_send_dbmail 
	@profile_name='DIMS_WorkFlow',
	@recipients= @recp,
	@subject=@sub,
	@body_format='html',
	@body=@message


END

GO
/****** Object:  StoredProcedure [dbo].[DIMS_REPORT_WORKFLOW_STATISTIC]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Atla Man Mohan Varma
-- Create date: 30th July 2017
-- Description:	Stored procedure to retrieve workflow statistic report
-- =============================================
CREATE PROCEDURE [dbo].[DIMS_REPORT_WORKFLOW_STATISTIC] 
@deptId NVARCHAR(50),
@divId NVARCHAR(50),
@fromDate NVARCHAR(30) = null,
@toDate NVARCHAR(30) = null
AS
declare @sqlstmt VARCHAR(4000);
BEGIN
	BEGIN TRY
		if( @divId = -1 and @fromDate is null and @toDate is null)
			begin
				SET @sqlstmt = 'SELECT count(*) As totalWorkflow,e.ECM_USER_NAME,a.WFL_WITM_RECEIVER_DIV,d.ECM_DIVISION,
				SUM(case when a.WFL_WITM_STATUS = ''New'' THEN 1 ELSE 0 END) AS NewWorkflow,  
				SUM(case when (a.WFL_WITM_TYPE = ''to'' AND a.WFL_WITM_SYS_STATUS =''ACTIVE'') THEN 1 ELSE 0 END) AS ActiveWorkflow,
				SUM(case when (a.WFL_WITM_DEADLINE < getDate() AND a.WFL_WITM_TYPE =''to'')THEN 1 ELSE 0 END) AS OverDueWorkflow
				FROM DIMS_WORKITEM a join ECM_EMPLOYEE e on a.WFL_RECIPIENT = e.ECM_USER_LOGIN JOIN ECM_DIVISION d on a.WFL_WITM_RECEIVER_DIV = d.ECM_DIVISION_CODE
				where a.WFL_WITM_SENDER_DEPT ='+@deptId+' AND a.WFL_WITM_STATUS <> ''Done'' GROUP BY WFL_WITM_RECEIVER_DIV,e.ECM_USER_NAME,d.ECM_DIVISION';
			end

		if( @divId != -1 and @fromDate is null and @toDate is null)
			begin 
				SET @sqlstmt = 'SELECT count(*) As totalWorkflow,e.ECM_USER_NAME,a.WFL_WITM_RECEIVER_DIV,d.ECM_DIVISION,
				SUM(case when a.WFL_WITM_STATUS = ''New'' THEN 1 ELSE 0 END) AS NewWorkflow,  
				SUM(case when (a.WFL_WITM_TYPE = ''to'' AND a.WFL_WITM_SYS_STATUS =''ACTIVE'') THEN 1 ELSE 0 END) AS ActiveWorkflow,
				SUM(case when (a.WFL_WITM_DEADLINE < getDate() AND a.WFL_WITM_TYPE =''to'')THEN 1 ELSE 0 END) AS OverDueWorkflow
				FROM DIMS_WORKITEM a join ECM_EMPLOYEE e on a.WFL_RECIPIENT = e.ECM_USER_LOGIN JOIN ECM_DIVISION d on a.WFL_WITM_RECEIVER_DIV = d.ECM_DIVISION_CODE
				where a.WFL_WITM_SENDER_DEPT ='+@deptId+' AND a.WFL_WITM_STATUS <> ''Done'' AND a.WFL_WITM_RECEIVER_DIV ='+@divId+'
				GROUP BY WFL_WITM_RECEIVER_DIV,e.ECM_USER_NAME,d.ECM_DIVISION';
			end

		if( @divId = -1 and @fromDate is not null and @toDate is not null )
			begin
				SET @sqlstmt = 'SELECT count(*) As totalWorkflow,e.ECM_USER_NAME,a.WFL_WITM_RECEIVER_DIV,d.ECM_DIVISION,
				SUM(case when a.WFL_WITM_STATUS = ''New'' THEN 1 ELSE 0 END) AS NewWorkflow,  
				SUM(case when (a.WFL_WITM_TYPE = ''to'' AND a.WFL_WITM_SYS_STATUS =''ACTIVE'') THEN 1 ELSE 0 END) AS ActiveWorkflow,
				SUM(case when (a.WFL_WITM_DEADLINE < getDate() AND a.WFL_WITM_TYPE =''to'')THEN 1 ELSE 0 END) AS OverDueWorkflow
				FROM DIMS_WORKITEM a join ECM_EMPLOYEE e on a.WFL_RECIPIENT = e.ECM_USER_LOGIN JOIN ECM_DIVISION d on a.WFL_WITM_RECEIVER_DIV = d.ECM_DIVISION_CODE
				where a.WFL_WITM_SENDER_DEPT ='+@deptId+' AND a.WFL_WITM_STATUS <> ''Done'' 
					 AND a.WFL_WITM_RECEIVEDON between DATEADD(dd,0,convert(datetime, '''+@fromDate+''',103))  and  DATEADD(dd,1,convert(datetime, '''+@toDate+''',103))	
				GROUP BY WFL_WITM_RECEIVER_DIV,e.ECM_USER_NAME,d.ECM_DIVISION';
			end

		if( @divId != -1 and @fromDate is not null and @toDate is not null)
			begin
				SET @sqlstmt = 'SELECT count(*) As totalWorkflow,e.ECM_USER_NAME,a.WFL_WITM_RECEIVER_DIV,d.ECM_DIVISION,
				SUM(case when a.WFL_WITM_STATUS = ''New'' THEN 1 ELSE 0 END) AS NewWorkflow,  
				SUM(case when (a.WFL_WITM_TYPE = ''to'' AND a.WFL_WITM_SYS_STATUS =''ACTIVE'') THEN 1 ELSE 0 END) AS ActiveWorkflow,
				SUM(case when (a.WFL_WITM_DEADLINE < getDate() AND a.WFL_WITM_TYPE =''to'')THEN 1 ELSE 0 END) AS OverDueWorkflow
				FROM DIMS_WORKITEM a join ECM_EMPLOYEE e on a.WFL_RECIPIENT = e.ECM_USER_LOGIN JOIN ECM_DIVISION d on a.WFL_WITM_RECEIVER_DIV = d.ECM_DIVISION_CODE
				where a.WFL_WITM_SENDER_DEPT ='+@deptId+' AND a.WFL_WITM_STATUS <> ''Done'' AND a.WFL_WITM_RECEIVER_DIV ='+@divId+'
					 AND a.WFL_WITM_RECEIVEDON between DATEADD(dd,0,convert(datetime, '''+@fromDate+''',103))  AND  DATEADD(dd,1,convert(datetime, '''+@toDate+''',103))	
				GROUP BY WFL_WITM_RECEIVER_DIV,e.ECM_USER_NAME,d.ECM_DIVISION';
			end

		if( @divId = -1 and @fromDate is not null and @toDate is null )
			begin
				SET @sqlstmt = 'SELECT count(*) As totalWorkflow,e.ECM_USER_NAME,a.WFL_WITM_RECEIVER_DIV,d.ECM_DIVISION,
				SUM(case when a.WFL_WITM_STATUS = ''New'' THEN 1 ELSE 0 END) AS NewWorkflow,  
				SUM(case when (a.WFL_WITM_TYPE = ''to'' AND a.WFL_WITM_SYS_STATUS =''ACTIVE'') THEN 1 ELSE 0 END) AS ActiveWorkflow,
				SUM(case when (a.WFL_WITM_DEADLINE < getDate() AND a.WFL_WITM_TYPE =''to'')THEN 1 ELSE 0 END) AS OverDueWorkflow
				FROM DIMS_WORKITEM a join ECM_EMPLOYEE e on a.WFL_RECIPIENT = e.ECM_USER_LOGIN JOIN ECM_DIVISION d on a.WFL_WITM_RECEIVER_DIV = d.ECM_DIVISION_CODE
				where a.WFL_WITM_SENDER_DEPT ='+@deptId+' AND a.WFL_WITM_STATUS <> ''Done'' 
					 AND a.WFL_WITM_RECEIVEDON between DATEADD(dd,0,convert(datetime, '''+@fromDate+''',103))  AND  DATEADD(dd,1,GETDATE())	
				GROUP BY WFL_WITM_RECEIVER_DIV,e.ECM_USER_NAME,d.ECM_DIVISION';
			end

		if( @divId != -1 and @fromDate is not null and @toDate is null)
			begin
				SET @sqlstmt = 'SELECT count(*) As totalWorkflow,e.ECM_USER_NAME,a.WFL_WITM_RECEIVER_DIV,d.ECM_DIVISION,
				SUM(case when a.WFL_WITM_STATUS = ''New'' THEN 1 ELSE 0 END) AS NewWorkflow,  
				SUM(case when (a.WFL_WITM_TYPE = ''to'' AND a.WFL_WITM_SYS_STATUS =''ACTIVE'') THEN 1 ELSE 0 END) AS ActiveWorkflow,
				SUM(case when (a.WFL_WITM_DEADLINE < getDate() AND a.WFL_WITM_TYPE =''to'')THEN 1 ELSE 0 END) AS OverDueWorkflow
				FROM DIMS_WORKITEM a join ECM_EMPLOYEE e on a.WFL_RECIPIENT = e.ECM_USER_LOGIN JOIN ECM_DIVISION d on a.WFL_WITM_RECEIVER_DIV = d.ECM_DIVISION_CODE
				where a.WFL_WITM_SENDER_DEPT ='+@deptId+' AND a.WFL_WITM_STATUS <> ''Done'' AND a.WFL_WITM_RECEIVER_DIV ='+@divId+'
					 AND a.WFL_WITM_RECEIVEDON between DATEADD(dd,0,convert(datetime, '''+@fromDate+''',103))  AND  DATEADD(dd,1,GETDATE())
				GROUP BY WFL_WITM_RECEIVER_DIV,e.ECM_USER_NAME,d.ECM_DIVISION';
			end

			if( @divId = -1 and @fromDate is null and @toDate is not null )
			begin
				SET @sqlstmt = 'SELECT count(*) As totalWorkflow,e.ECM_USER_NAME,a.WFL_WITM_RECEIVER_DIV,d.ECM_DIVISION,
				SUM(case when a.WFL_WITM_STATUS = ''New'' THEN 1 ELSE 0 END) AS NewWorkflow,  
				SUM(case when (a.WFL_WITM_TYPE = ''to'' AND a.WFL_WITM_SYS_STATUS =''ACTIVE'') THEN 1 ELSE 0 END) AS ActiveWorkflow,
				SUM(case when (a.WFL_WITM_DEADLINE < getDate() AND a.WFL_WITM_TYPE =''to'')THEN 1 ELSE 0 END) AS OverDueWorkflow
				FROM DIMS_WORKITEM a join ECM_EMPLOYEE e on a.WFL_RECIPIENT = e.ECM_USER_LOGIN JOIN ECM_DIVISION d on a.WFL_WITM_RECEIVER_DIV = d.ECM_DIVISION_CODE
				where a.WFL_WITM_SENDER_DEPT ='+@deptId+' AND a.WFL_WITM_STATUS <> ''Done'' 
					 AND a.WFL_WITM_RECEIVEDON <= DATEADD(dd,1,convert(datetime, '''+@toDate+''',103))	
				GROUP BY WFL_WITM_RECEIVER_DIV,e.ECM_USER_NAME,d.ECM_DIVISION';
			end

		if( @divId != -1 and @fromDate is null and @toDate is not null)
			begin
				SET @sqlstmt = 'SELECT count(*) As totalWorkflow,e.ECM_USER_NAME,a.WFL_WITM_RECEIVER_DIV,d.ECM_DIVISION,
				SUM(case when a.WFL_WITM_STATUS = ''New'' THEN 1 ELSE 0 END) AS NewWorkflow,  
				SUM(case when (a.WFL_WITM_TYPE = ''to'' AND a.WFL_WITM_SYS_STATUS =''ACTIVE'') THEN 1 ELSE 0 END) AS ActiveWorkflow,
				SUM(case when (a.WFL_WITM_DEADLINE < getDate() AND a.WFL_WITM_TYPE =''to'')THEN 1 ELSE 0 END) AS OverDueWorkflow
				FROM DIMS_WORKITEM a join ECM_EMPLOYEE e on a.WFL_RECIPIENT = e.ECM_USER_LOGIN JOIN ECM_DIVISION d on a.WFL_WITM_RECEIVER_DIV = d.ECM_DIVISION_CODE
				where a.WFL_WITM_SENDER_DEPT ='+@deptId+' AND a.WFL_WITM_STATUS <> ''Done'' AND a.WFL_WITM_RECEIVER_DIV ='+@divId+'
					 AND a.WFL_WITM_RECEIVEDON <=  DATEADD(dd,1,convert(datetime, '''+@toDate+''',103))	
				GROUP BY WFL_WITM_RECEIVER_DIV,e.ECM_USER_NAME,d.ECM_DIVISION';
			end

		EXEC (@sqlstmt)
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR NULL, NULL, 'ERROR', 'APP';
		THROW 513, 'Database error while getting workflow statistics', 1;
	END CATCH
END




GO
/****** Object:  StoredProcedure [dbo].[GET_ARCHIVE_WORKITEM_DETAILS]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,Atla Man Mohan Varma>
-- Create date: <Create Date,20 March 2017>
-- Description:	<Description, This Procedure return all the workitems for a user under archive item>
-- =============================================
CREATE PROCEDURE [dbo].[GET_ARCHIVE_WORKITEM_DETAILS] 
	-- Add the parameters for the stored procedure here
	@userId VARCHAR(50),
	@pageNo VARCHAR(50),
	@pageSize VARCHAR(50),
	@rowNo int = 1,
	@FilterType VARCHAR(15) = NULL,
	@userFolderName VARCHAR(50) = NULL,
	@sortOrder VARCHAR(5) = NULL,
	@sortColumn VARCHAR(80) = NULL
As
	declare @sqlstmt VARCHAR(4000);
BEGIN
	BEGIN TRY
		IF(@userFolderName IS NULL)
		BEGIN
			SET @sqlstmt = 'SELECT *
			FROM 
			( SELECT ROW_NUMBER() OVER(ORDER BY '+@sortColumn+' '+@sortOrder+') as RowNum, COUNT(*) over() AS totalRecordsNumber,A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,C.ECM_USER_NAME AS SENDER_NAME, 
								A.WFL_WITM_TYPE, A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS,B.WFL_PRIORITY, 
								A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS FROM DIMS_WORKITEM A 
								JOIN DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID 
								JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN 
								WHERE A.WFL_RECIPIENT = ''' + @userId + ''' AND (A.WFL_WITM_STATUS=''Archive'' OR A.WFL_WITM_STATUS=''Complete'') 
									AND A.WFL_WITM_TYPE=''CC'') as Archive where RowNum between ((('+@pageNo+' - 1) * '+@pageSize+' )+ 1) and ('+@pageNo+' * '+@pageSize+' )';
	
		END

		ELSE IF(@userFolderName IS NOT NULL)
		BEGIN
			SET @sqlstmt = 'SELECT *
			FROM 
			( SELECT ROW_NUMBER() OVER(ORDER BY '+@sortColumn+' '+@sortOrder+') as RowNum, COUNT(*) over() AS totalRecordsNumber,A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,C.ECM_USER_NAME AS SENDER_NAME, 
								A.WFL_WITM_TYPE, A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS,B.WFL_PRIORITY, 
								A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS FROM DIMS_WORKITEM A 
								JOIN DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID 
								JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN 
								WHERE A.WFL_RECIPIENT = ''' + @userId + ''' AND A.WFL_SENDER ='''+@userFolderName+''' AND (A.WFL_WITM_STATUS=''Archive'' OR A.WFL_WITM_STATUS=''Complete'') 
									AND A.WFL_WITM_TYPE=''CC'') as Archive where RowNum between ((('+@pageNo+' - 1) * '+@pageSize+' )+ 1) and ('+@pageNo+' * '+@pageSize+' )';
	
		END

		EXEC (@sqlstmt)
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR NULL, NULL, 'ERROR', 'APP';
		THROW 514, 'Database error while getting archived work item details', 1;
	END CATCH
END






GO
/****** Object:  StoredProcedure [dbo].[GET_INBOX_WORKITEM_DETAILS]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,Atla Man Mohan Varma>
-- Create date: <Create Date,20 March 2017>
-- Description:	<Description, This Procedure return all the workitems for a user under sent item>
-- =============================================
CREATE PROCEDURE [dbo].[GET_INBOX_WORKITEM_DETAILS] 
	-- Add the parameters for the stored procedure here
   @userId VARCHAR(50),
	@pageNo VARCHAR(50),
	@pageSize VARCHAR(50),
	@rowNo int = 1,
	@FilterType VARCHAR(15) ,
	@userFolderName VARCHAR(50) = NULL,
	@sortOrder VARCHAR(5) = NULL,
	@sortColumn VARCHAR(80) = NULL
As
	declare @sqlstmt VARCHAR(4000);

	declare @whereclause VARCHAR(4000);
	declare @folderclause VARCHAR(200);
	declare @folderLogin NVARCHAR(200);
BEGIN
	
	SET @sqlstmt = 'SELECT *
FROM 
( SELECT ROW_NUMBER() OVER(ORDER BY '+@sortColumn+' '+@sortOrder+') as RowNum, COUNT(*) over() AS totalRecordsNumber,A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,C.ECM_USER_NAME AS SENDER_NAME, A.WFL_WITM_TYPE,A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,A.WFL_WITM_STATUS,B.WFL_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS,A.WFL_INSTRUCTIONS,B.WFL_NAME,A.WFL_WITM_ROOT_SENDER,A.WFL_WITM_SENDER_DEPT,A.WFL_WITM_SENDER_DIV,C.ECM_DEPARTMENT,C.ECM_DIVISION FROM DIMS_WORKITEM A JOIN DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN WHERE ';

	SET @whereclause = '';
	IF ( @userFolderName IS NOT NUll) 
	BEGIN
		
		SET @folderclause = ' A.WFL_SENDER = '+ char(39) + @userFolderName + char(39);
	END
	
	SET @FilterType = (SELECT RTRIM(LTRIM((UPPER(@FilterType)))))

	IF(@FilterType = 'ACTIVE')
	BEGIN
		SET @whereclause = @whereclause+' A.WFL_RECIPIENT='''+@userId+''' AND A.WFL_WITM_TYPE = ''TO'' AND A.WFL_WITM_SYS_STATUS = ''ACTIVE'' AND A.WFL_WITM_STATUS <> ''Archive''';
	END

	ELSE IF(@FilterType = 'ALL')
	BEGIN
		SET @whereclause = @whereclause+' A.WFL_RECIPIENT='''+@userId+''' AND A.WFL_WITM_SYS_STATUS = ''ACTIVE'' AND A.WFL_WITM_STATUS <> ''Archive''';
	END

	ELSE IF(@FilterType = 'CC')
	BEGIN
		SET @whereclause = @whereclause+' A.WFL_RECIPIENT='''+@userId+''' AND A.WFL_WITM_TYPE=''CC'' AND A.WFL_WITM_SYS_STATUS = ''ACTIVE'' AND A.WFL_WITM_STATUS <> ''Archive''';
	END

	ELSE IF(@FilterType = 'DONE BY SUB' OR @FilterType = 'SUB')
	BEGIN
		SET @whereclause = @whereclause+' A.WFL_RECIPIENT='''+@userId+''' AND A.WFL_WITM_TYPE=''Reply'' AND A.WFL_WITM_SYS_STATUS = ''ACTIVE'' AND A.WFL_WITM_STATUS <> ''Archive''';
	END

	ELSE IF(@FilterType = 'OVERDUE')
	BEGIN
		SET @whereclause = @whereclause+'  A.WFL_RECIPIENT='''+@userId+''' AND A.WFL_WITM_TYPE = ''TO'' AND A.WFL_WITM_SYS_STATUS = ''ACTIVE'' AND A.WFL_WITM_DEADLINE < getDate() AND A.WFL_WITM_STATUS <> ''Archive''';
	END
	ELSE IF(@FilterType = 'NEW')
	BEGIN
		SET @whereclause = @whereclause+' A.WFL_RECIPIENT='''+@userId+''' AND A.WFL_WITM_STATUS = ''New'' AND A.WFL_WITM_SYS_STATUS = ''ACTIVE''AND A.WFL_WITM_STATUS <> ''Archive''';
	END
	ELSE 
	BEGIN
		SET @whereclause = @whereclause+' A.WFL_RECIPIENT='''+@userId+''' AND A.WFL_WITM_SYS_STATUS = ''ACTIVE'' AND A.WFL_WITM_STATUS <> ''Archive''';
	END

	IF ( @userFolderName IS NOT NUll) 
	BEGIN
		SET @sqlstmt = @sqlstmt + @folderclause;
		if (LEN(@whereclause) > 0)
			SET @sqlstmt = @sqlstmt + ' AND ';
	END 
	SET @sqlstmt = @sqlstmt + @whereclause+' ) as inbox where RowNum between ((('+@pageNo+' - 1) * '+@pageSize+' )+ 1) and ('+@pageNo+' * '+@pageSize+' )';
	PRINT @sqlstmt

	EXEC (@sqlstmt)
END








GO
/****** Object:  StoredProcedure [dbo].[GET_SENT_ITEM_RECIPIENTS]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Susanth Kurunthil>
-- Create date: <13-June-2017>
-- Description:	<Procudeure to get the sent item recipient list>
-- =============================================
CREATE PROCEDURE [dbo].[GET_SENT_ITEM_RECIPIENTS]
@WitmID NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;

    DECLARE @wflID NVARCHAR(50)
	DECLARE @parentItem NVARCHAR(50)
	
	SELECT @wflID = WFL_ID, @parentItem = WFL_PARENT_WITM from DIMS_WORKITEM where WFL_WITM_ID = @WitmID

	IF(@parentItem IS NULL)
		SELECT WFL_RECIPIENT, ECM_USER_NAME, WFL_WITM_TYPE,WFL_WITM_STATUS FROM DIMS_WORKITEM, ECM_EMPLOYEE 
		WHERE WFL_ID = @wflID AND WFL_PARENT_WITM IS NULL
		AND WFL_RECIPIENT = ECM_EMPLOYEE.ECM_USER_LOGIN AND WFL_WITM_SYS_STATUS = 'ACTIVE'
	ELSE
		SELECT WFL_RECIPIENT, ECM_USER_NAME, WFL_WITM_TYPE,WFL_WITM_STATUS FROM DIMS_WORKITEM, ECM_EMPLOYEE 
		WHERE WFL_ID = @wflID AND WFL_PARENT_WITM = @parentItem
		AND WFL_RECIPIENT = ECM_EMPLOYEE.ECM_USER_LOGIN AND WFL_WITM_SYS_STATUS = 'ACTIVE'
END




GO
/****** Object:  StoredProcedure [dbo].[GET_SENT_ITEM_SENDER]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[GET_SENT_ITEM_SENDER] 
@WitmID NVARCHAR(50),
@Recipient NVARCHAR(50) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WflID NVARCHAR(50)
	BEGIN TRY
		DECLARE @RootSender NVARCHAR(50)
		DECLARE @NodeNo NVARCHAR(1024)
		
		SELECT @WflID = WFL_ID, @Recipient = WFL_SENDER,
				@RootSender = WFL_WITM_ROOT_SENDER,
				@NodeNo = WFL_WITM_NODE  
				FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID

	
		DECLARE @wiRecipient NVARCHAR(50)
		DECLARE @oldNodeNo NVARCHAR(MAX)
		SET @wiRecipient = @Recipient
		SET @oldNodeNo = @NodeNo
		IF LEN(@NodeNo) > 3
			BEGIN
				SET @NodeNo = (SELECT LEFT(@NodeNo, LEN(@NodeNo) - 3))
				IF(SELECT COUNT(*) FROM DBO.DIMS_WORKITEM WHERE WFL_ID =  @WflID AND WFL_WITM_NODE = @oldNodeNo) > 1
				BEGIN			
					SET @Recipient = (SELECT TOP(1) WFL_RECIPIENT FROM DBO.DIMS_WORKITEM WHERE WFL_ID =  @WflID AND WFL_WITM_NODE = @NodeNo AND WFL_WITM_TYPE <> 'Reply' AND WFL_WITM_STATUS <> 'Done')
					IF(@Recipient IS NULL)
						SET @Recipient = @wiRecipient
				END
				ELSE
					SET @Recipient = @wiRecipient
			END
		ELSE
			SET @Recipient = @RootSender
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 50516, 'Database error while getting sent work item sender', 1;
	END CATCH	
END


GO
/****** Object:  StoredProcedure [dbo].[GET_SENT_WORKITEM_DETAILS]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- Batch submitted through debugger: SQLQuery9.sql|7|0|C:\Users\p8admin\AppData\Local\Temp\1\~vsC651.sql

-- =============================================
-- Author:		<Author,Atla Man Mohan Varma>
-- Create date: <Create Date,20 March 2017>
-- Description:	<Description, This Procedure return all the workitems for a user under sent item>
-- =============================================
CREATE PROCEDURE [dbo].[GET_SENT_WORKITEM_DETAILS] 
	-- Add the parameters for the stored procedure here
	@userId VARCHAR(50),
	@pageNo VARCHAR(50),
	@pageSize VARCHAR(50),
	@rowNo int = 1,
	@FilterType VARCHAR(15) ,
	@userFolderName VARCHAR(50) = NULL,
	@sortOrder VARCHAR(5) = NULL,
	@sortColumn VARCHAR(80) = NULL
As
	declare @sqlstmt VARCHAR(4000);
	declare @folderLogin NVARCHAR(50);
BEGIN
	BEGIN TRY
		SET @sqlstmt = 'SELECT *
	FROM 
	( SELECT ROW_NUMBER() OVER(ORDER BY '+@sortColumn+' '+@sortOrder+') as RowNum, COUNT(*) over() AS totalRecordsNumber,A.WFL_WITM_ID,A.WFL_WITM_RECEIVEDON, 
			A.WFL_ID,B.WFL_SUBJECT,A.WFL_SENDER,C.ECM_USER_NAME AS RECIPIENT_NAME,
			A.WFL_WITM_TYPE,A.WFL_STEP_NO,A.WFL_WITM_DEADLINE,A.WFL_WITM_STATUS,B.WFL_PRIORITY,A.WFL_RECIPIENT,A.WFL_WITM_SYS_STATUS,A.WFL_WITM_ROOT_SENDER,A.WFL_WITM_SENDER_DEPT,A.WFL_WITM_SENDER_DIV,C.ECM_DEPARTMENT,C.ECM_DIVISION 
			FROM DIMS_WORKITEM A JOIN DIMS_WORKFLOW B ON (A.WFL_ID=B.WFL_ID) JOIN ECM_EMPLOYEE C ON (A.WFL_SENDER=C.ECM_USER_LOGIN) WHERE ';
	
		IF ( @userFolderName IS NOT NUll) 
		BEGIN
		
			SET @sqlstmt = @sqlstmt+' WFL_WITM_SEQNO <= 1 AND A.WFL_RECIPIENT ='''+@userFolderName+''' AND ';
		END
	
		SET @FilterType = (SELECT RTRIM(LTRIM((UPPER(@FilterType)))))

		IF(@FilterType = 'ACTIVE')
		BEGIN
			SET @sqlstmt = @sqlstmt+' WFL_WITM_SEQNO <= 1 AND A.WFL_SENDER = ''' + @userId + ''' AND A.WFL_WITM_TYPE =''TO'' AND A.WFL_WITM_STATUS != ''Complete''';
		END

		ELSE IF(@FilterType = 'ALL')
		BEGIN
			SET @sqlstmt = @sqlstmt+' WFL_WITM_SEQNO <= 1 AND A.WFL_SENDER = ''' + @userId + ''' AND A.WFL_WITM_STATUS != ''Complete''';
		END

		ELSE IF(@FilterType = 'CC')
		BEGIN
			SET @sqlstmt = @sqlstmt+' WFL_WITM_SEQNO <= 1 AND A.WFL_SENDER = '''+ @userId +''' AND A.WFL_WITM_TYPE =''CC'' AND A.WFL_WITM_STATUS != ''Complete''';
		END

		ELSE IF(@FilterType = 'DONE BY ME')
		BEGIN
			SET @sqlstmt = @sqlstmt+' WFL_WITM_SEQNO <= 1 AND A.WFL_SENDER = ''' + @userId + ''' AND A.WFL_WITM_TYPE =''Reply'' AND A.WFL_WITM_STATUS != ''Complete''';
		END

		ELSE IF(@FilterType = 'OVERDUE')
		BEGIN
			SET @sqlstmt = @sqlstmt+' WFL_WITM_SEQNO <= 1 AND A.WFL_SENDER = ''' + @userId + ''' AND A.WFL_WITM_TYPE <>(''CC'') AND A.WFL_WITM_STATUS != ''Complete'''
		END
		ELSE
		BEGIN
			SET @sqlstmt = @sqlstmt+' WFL_WITM_SEQNO <= 1 AND A.WFL_SENDER = ''' + @userId + ''' AND A.WFL_WITM_STATUS != ''Complete''';
		END

		SET @sqlstmt = @sqlstmt +' ) as Sent where RowNum between ((('+@pageNo+' - 1) * '+@pageSize+' )+ 1) and ('+@pageNo+' * '+@pageSize+' )';
		EXEC (@sqlstmt)
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR NULL, NULL, 'ERROR', 'APP';
		THROW 50517, 'Database error while getting sent work item details', 1;
	END CATCH	
	
END









GO
/****** Object:  StoredProcedure [dbo].[get_user_dept_div_list]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author, Atla Man Mohan Varma>
-- Create date: <Create Date, 20 March 2017>
-- Description:	<Description, This procedure will launch the workflow and store the workitem,history and attachment details>
-- =============================================
CREATE PROCEDURE [dbo].[get_user_dept_div_list]
	
	
	@division_code int=0,
	@department_code int=0,
	@searchCrtieria VARCHAR(250) = NULL
	
AS
BEGIN
	SET NOCOUNT ON
	DECLARE @sqlstmt VARCHAR(4000);
	DECLARE @crtieria VARCHAR(4000);
	
	BEGIN TRY
		IF (@searchCrtieria IS NOT NULL)
		BEGIN
		SET @crtieria =' AND (ECM_USER_NAME LIKE ''%'+@searchCrtieria+'%''  OR ECM_USER_LOGIN LIKE ''%'+@searchCrtieria+'%'' OR ECM_DESIGNATION LIKE ''%'+@searchCrtieria+'%'' OR ECM_USER_TITLE LIKE ''%'+@searchCrtieria+'%'')';
		END

		IF (@searchCrtieria IS NULL)
		BEGIN
		SET @crtieria ='';
		END

		IF ( @division_code <> 0) 
		BEGIN
			
		SET @sqlstmt = 'SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,ECM_EMPLOYEE.ECM_USER_EMAIL,
				ECM_EMPLOYEE.ECM_DESIGNATION,ECM_EMPLOYEE.ECM_USER_TITLE FROM ECM_EMPLOYEE 
				WHERE ECM_EMPLOYEE.ECM_DIVISION_CODE = '+convert(varchar(10),@division_code)+' AND (ECM_EMPLOYEE.ECM_ACTIVE_USER=''true'' OR ECM_EMPLOYEE.ECM_ACTIVE_USER=1)'+@crtieria;
			
		END
		
		IF ( @department_code <> 0) 
		BEGIN
			
		SET @sqlstmt = 'SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,ECM_EMPLOYEE.ECM_USER_EMAIL,
				ECM_EMPLOYEE.ECM_DESIGNATION,ECM_EMPLOYEE.ECM_USER_TITLE FROM ECM_EMPLOYEE 
				WHERE ECM_EMPLOYEE.ECM_DEPT_CODE = '+convert(varchar(10),@department_code)+' AND (ECM_EMPLOYEE.ECM_ACTIVE_USER=''true'' OR ECM_EMPLOYEE.ECM_ACTIVE_USER=1)'+@crtieria;
			
		END

		EXEC (@sqlstmt)
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR NULL, NULL, 'ERROR', 'APP';
		THROW 50518, 'Database error while getting user department/division list', 1;
	END CATCH
END






GO
/****** Object:  StoredProcedure [dbo].[GET_USER_FILTER]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[GET_USER_FILTER] 
	@filter nvarchar(50),
	@deptCode nvarchar(50)
	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
    -- Insert statements for procedure here
		DECLARE @sqlstmt nvarchar(200);
		SET @sqlstmt = 'SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME FROM ECM_EMPLOYEE WHERE ECM_DEPT_CODE ='+@deptCode;
		if(@filter = 'All')
		BEGIN
		SET @sqlstmt = @sqlstmt;
			
		END
		else if (@filter = 'TL') 
		BEGIN
		SET @sqlstmt = @sqlstmt + ' AND ECM_JOB_TITLE = ''' + @filter+ '''';
		END
		else if (@filter = 'senior') 
		BEGIN
		SET @sqlstmt = @sqlstmt + ' AND (ECM_JOB_TITLE = ''MGR'' OR ECM_JOB_TITLE = ''DCEO'') ';
		END

		EXEC (@sqlstmt)
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR NULL, NULL, 'ERROR', 'APP';
		THROW 519, 'Database error while getting user filter', 1;
	END CATCH
END



GO
/****** Object:  StoredProcedure [dbo].[GET_WORKFLOW_HISTORY]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author, Atla Man Mohan Varma>
-- Create date: <Create Date, 20 March 2017>
-- Description:	<Description, This procedure will launch the workflow and store the workitem,history and attachment details>
-- =============================================
CREATE PROCEDURE [dbo].[GET_WORKFLOW_HISTORY]
	
	@workItemId VARCHAR(50) = NULL,
	@workFlowID VARCHAR(50) = NULL
	
AS
	DECLARE @sqlstmt VARCHAR(4000);
	
	BEGIN
		BEGIN TRY
			IF ( @workFlowID IS NULL) 
			BEGIN
			
				SELECT 
				@workFlowID = WFL_ID 
				FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @workItemId
			
			END
		
			SET @sqlstmt = 'SELECT C.ECM_USER_NAME AS SENDER_NAME,D.ECM_USER_NAME AS RECIPIENT_NAME,
							A.WFL_ACTION_STATUS ,B.WFL_WITM_TYPE,B.WFL_WITM_RECEIVEDON,B.WFL_WITM_DEADLINE,
							B.WFL_INSTRUCTIONS,B.WFL_WITM_ACTION_COMMENT,A.WFL_ACTION_USER,A.WFL_ACTION_TIMESTAMP,
							A.WFL_ID,B.WFL_WITM_ID FROM DIMS_WORKFLOW_HISTORY A JOIN DIMS_WORKITEM B 
							ON A.WFL_ACTION_WITM_ID = B.WFL_WITM_ID JOIN ECM_EMPLOYEE C 
							ON B.WFL_SENDER=C.ECM_USER_LOGIN JOIN ECM_EMPLOYEE D  
							ON (B.WFL_RECIPIENT=D.ECM_USER_LOGIN OR B.WFL_RECIPIENT=D.ECM_USER_NAME) WHERE A.WFL_ID = '''+@workFlowID+''' 
							ORDER BY A.WFL_ACTION_TIMESTAMP ASC'
		
			EXEC (@sqlstmt)
		END TRY
		BEGIN CATCH
			EXECUTE DIMS_LOG_ERROR @workflowid, @workItemId, 'ERROR', 'APP';
			THROW 520, 'Database error while getting workflow history', 1;
		END CATCH
	END






GO
/****** Object:  StoredProcedure [dbo].[GET_WORKITEM_ATTACHMENTS]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Susanth Kurunthil
-- Create date: 17-JUL-2017
-- Description:	Fetch Work Item Attachments
-- =============================================
CREATE PROCEDURE [dbo].[GET_WORKITEM_ATTACHMENTS]
	@WitmID VARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;
	BEGIN TRY
		DECLARE @WiCount INT
		SELECT @WiCount = COUNT(*) FROM DIMS_WORKITEM_ATTACHMENT WHERE WFL_WITM_ID = @WitmID
		IF(@WiCount > 0)
		BEGIN
			SELECT WFL_DOCUMENT_ID, WITM_ATTACHMENT_TYPE AS WFL_ATTACHMENT_TYPE 
			FROM DIMS_WORKITEM_ATTACHMENT WHERE WFL_WITM_ID = @WitmID
			ORDER BY WITM_ATTACHMENT_TYPE DESC
		END
		ELSE
		BEGIN
			DECLARE @WflID NVARCHAR(50)
			SET @WflID = (SELECT TOP(1) WFL_ID FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID)
			SELECT WFL_DOCUMENT_ID, WFL_ATTACHMENT_TYPE FROM DIMS_WORKFLOW_ATTACHMENT 
			WHERE WFL_ID = @WflID ORDER BY WFL_ATTACHMENT_TYPE DESC
		END
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR NULL, @WitmID, 'ERROR', 'APP';
		THROW 521, 'Database error while getting work item attachments', 1;
	END CATCH
END




GO
/****** Object:  StoredProcedure [dbo].[GET_WORKITEM_DETAILS]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author, Atla Man Mohan Varma>
-- Create date: <Create Date, 20 March 2017>
-- Description:	<Description, This procedure will launch the workflow and store the workitem,history and attachment details>
-- =============================================
CREATE PROCEDURE [dbo].[GET_WORKITEM_DETAILS]
	
	@workItemId VARCHAR(50),
	@workflowHistroyUUID VARCHAR(50)
	
AS

	DECLARE @sqlstmt VARCHAR(4000);
	
	BEGIN
		DECLARE @WorkItemRecipient VARCHAR(50)
		DECLARE @WorkItemStatus VARCHAR(10)
		DECLARE @WorkFlowID VARCHAR(50)
		BEGIN TRY
			SELECT 
			@WorkItemRecipient = WFL_RECIPIENT,
			@WorkFlowID = WFL_ID,
			@WorkItemStatus = WFL_WITM_STATUS 
			FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @workItemId;
		
			IF ( @WorkItemStatus ='New') 
			BEGIN
				UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS = 'Read',
							WFL_WITM_ACTION_BY = @WorkItemRecipient
							 WHERE WFL_WITM_ID = @workItemId;
						 
						 
				INSERT INTO DIMS_WORKFLOW_HISTORY
				(WFL_HISTORY_ID,WFL_ID,WFL_ACTION_USER,WFL_ACTION_TIMESTAMP,WFL_ACTION_WITM_ID,WFL_ACTION_DETAILS,WFL_ACTION_DOCUMENT,WFL_ACTION_STATUS)
				VALUES(@workflowHistroyUUID,@WorkFlowID,@WorkItemRecipient,getDate(),@workItemId,'Read',null,'Read');
			END
		
			SET @sqlstmt = 'SELECT A.WFL_WITM_ID,A.WFL_SENDER,A.WFL_ID,B.WFL_SUBJECT,B.WFL_PRIORITY,C.ECM_USER_NAME AS 	SENDER_NAME,A.WFL_WITM_TYPE,A.WFL_STEP_NO,A.WFL_PARENT_WITM,A.WFL_INSTRUCTIONS,
			A.WFL_WITM_RECEIVEDON,A.WFL_WITM_DEADLINE,A.WFL_WITM_REMINDER_DATE,A.WFL_WITM_ROOT_SENDER,  A.WFL_WITM_STATUS,A.WFL_WITM_ACTION,A.WFL_WITM_ACTION_COMMENT,A.WFL_WITM_ACTION_BY,
			A.WFL_WITM_ACTION_ONBEHALF,A.WFL_WITM_SENDER_DEPT,A.WFL_WITM_RECEIVER_DEPT,A.WFL_WITM_SENDER_DIV,
			A.WFL_WITM_RECEIVER_DIV,A.WFL_WITM_SYS_STATUS,B.WFL_NAME FROM DIMS_WORKITEM A JOIN DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID 
			JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN WHERE A.WFL_WITM_ID = '''+ @workItemId+ '''';
		
			EXEC (@sqlstmt)
		END TRY
		BEGIN CATCH
			EXECUTE DIMS_LOG_ERROR @workflowid, @workItemId, 'ERROR', 'APP';
			THROW 523, 'Database error while getting work item details', 1;
		END CATCH
	END






GO
/****** Object:  StoredProcedure [dbo].[GET_WORKITEM_RECIPIENTS]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author, Atla Man Mohan Varma>
-- Create date: <Create Date, 20 March 2017>
-- Description:	<Description, This procedure will launch the workflow and store the workitem,history and attachment details>
-- =============================================
CREATE PROCEDURE [dbo].[GET_WORKITEM_RECIPIENTS]
	
	@workItemId VARCHAR(50)
	
AS
	DECLARE @sqlstmt VARCHAR(4000);
	
	BEGIN
		BEGIN TRY	
			SET @sqlstmt = 'SELECT A.WFL_RECIPIENT,A.WFL_WITM_TYPE,C.ECM_USER_NAME AS RECIPIENT_NAME
							FROM DIMS_WORKITEM A JOIN DIMS_WORKFLOW B ON A.WFL_ID = B.WFL_ID
							JOIN ECM_EMPLOYEE C ON A.WFL_RECIPIENT = C.ECM_USER_LOGIN
							WHERE A.WFL_ID IN (select WFL_ID from DIMS_WORKITEM where WFL_WITM_ID= '''+ @workItemId+ ''')'
		
			EXEC (@sqlstmt)
		END TRY
		BEGIN CATCH
			EXECUTE DIMS_LOG_ERROR NULL, @workItemId, 'ERROR', 'APP';
			THROW 524, 'Database error while getting work item recipients', 1;
		END CATCH
	END






GO
/****** Object:  StoredProcedure [dbo].[SYNC_DIMS_ADD_ATTACHMENT_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SYNC_DIMS_ADD_ATTACHMENT_WORKITEM]
@WitmID NVARCHAR(50),
@DocumentID NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WfID NVARCHAR(50)
	BEGIN TRY
		SELECT @WfID = WFL_ID FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID

		DECLARE @AttID NVARCHAR(50)
		SET @AttID = NEWID()

		DECLARE @ExistID NVARCHAR(50)
		DECLARE @Type NVARCHAR(10)
		SELECT @ExistID = WFL_ATTACHMENT_ID, @Type = WFL_ATTACHMENT_TYPE FROM DIMS_WORKFLOW_ATTACHMENT
		WHERE WFL_ID = @WfID AND WFL_DOCUMENT_ID = @DocumentID

		IF(@ExistID IS NULL)
		BEGIN
			SET @ExistID = (SELECT TOP(1) WFL_ATTACHMENT_ID FROM DIMS_WORKFLOW_ATTACHMENT
							WHERE WFL_ID = @WfID)
			IF(@ExistID IS NULL)
				SET @Type = 'PRIMARY'
			ELSE
				SET @Type = 'ATTACHMENT'
	
			INSERT INTO DIMS_WORKFLOW_ATTACHMENT
					(WFL_ATTACHMENT_ID, WFL_ID, WFL_DOCUMENT_ID, WFL_ATTACHMENT_TYPE)
					VALUES
					(@AttID, @WfID, @DocumentID, @Type)
		END

		DECLARE @WitmAttID NVARCHAR(50)
		SET @WitmAttID = NEWID()
		DECLARE @ExistID1 NVARCHAR(50)
		SELECT @ExistID1 = WITM_ATTACHMENT_ID FROM DIMS_WORKITEM_ATTACHMENT
		WHERE WFL_ID = @WfID AND WFL_DOCUMENT_ID = @DocumentID AND WFL_WITM_ID = @WitmID
		IF(@ExistID1 IS NULL)
		BEGIN
			INSERT INTO DIMS_WORKITEM_ATTACHMENT
					(WITM_ATTACHMENT_ID, WFL_ID, WFL_WITM_ID, WFL_DOCUMENT_ID, WITM_ATTACHMENT_TYPE)
					VALUES
					(@WitmAttID, @WfID, @WitmID, @DocumentID, @Type)
		END
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, @WitmID, 'ERROR', 'APP';
		THROW 502, 'Database error while adding attachment to work item', 1;
	END CATCH
END


GO
/****** Object:  StoredProcedure [dbo].[SYNC_DIMS_ADDUSER_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SYNC_DIMS_ADDUSER_WORKITEM]
@InWiID NVARCHAR(50),
@Sender NVARCHAR(50),
@Recipient NVARCHAR(50),
@Type NVARCHAR(10),
@Instructions NVARCHAR(255),
@Deadline NVARCHAR(30) = null,
@Reminder NVARCHAR(30) = null,
@SentOnBehalf NVARCHAR(50) = null,
@Status NVARCHAR(20) = null,
@Comments NVARCHAR(1024) = null,
@OLDID INT = 0
AS
BEGIN
	SET NOCOUNT ON;
DECLARE @WitmID NVARCHAR(50)
	DECLARE @WfID NVARCHAR(50)
	DECLARE @Parent NVARCHAR(50)
	DECLARE @ExtWitm NVARCHAR(50)
	DECLARE @NodeNo NVARCHAR(1024)
	DECLARE @SeqNo INT = 0
	BEGIN TRY
		SELECT @WfID = WFL_ID, @Parent = WFL_PARENT_WITM , @NodeNo = WFL_WITM_NODE
		FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @InWiID

		IF (@Parent IS NULL)
			BEGIN
				SELECT @ExtWitm = WFL_WITM_ID FROM DIMS_WORKITEM WHERE WFL_ID = @WfID AND WFL_RECIPIENT = @Recipient AND WFL_PARENT_WITM IS NULL
				SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM IS NULL AND WFL_ID = @WfID) + 1
				SET @NodeNo = (SELECT FORMAT(@SeqNo, '00#'))
			END
		ELSE
			BEGIN
				SELECT @ExtWitm = WFL_WITM_ID FROM DIMS_WORKITEM WHERE WFL_ID = @WfID AND WFL_RECIPIENT = @Recipient AND WFL_PARENT_WITM = @Parent
				SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent) + 1
				SET @NodeNo = (SELECT CONCAT(@NodeNo, FORMAT(@SeqNo, '00#')))
			END
		IF @ExtWitm IS NOT NULL
			THROW 1002, 'The recipient already exists', 1;

		SET @WitmID = NEWID()

		DECLARE @DeadlineDate DATETIME
		DECLARE @ReminderDate DATETIME

		IF (@Deadline IS NULL) OR (LEN(@Deadline) <= 0)
			SET @DeadlineDate = DATEADD(day,5,GETDATE())
		ELSE
			SET @DeadlineDate = convert(datetime, @Deadline,103)

		if (@Reminder IS NULL) OR (LEN(@Reminder) <= 0)
			SET @ReminderDate = DATEADD(day, -1, @DeadlineDate)
		ELSE
			SET @ReminderDate = convert(datetime, @Reminder,103)

		IF(@SentOnBehalf IS NULL)
			SET @SentOnBehalf = @Sender

		DECLARE @StepNo INT = 1
		DECLARE @RootSender NVARCHAR(50) = @SentOnBehalf
		IF(@Parent IS NOT NULL)
		BEGIN
			SET @StepNo = (SELECT TOP(1) WFL_STEP_NO FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent) + 1
			SET @RootSender = (SELECT TOP(1) WFL_WITM_ROOT_SENDER FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent)
		END 

		DECLARE @SenderDept INT
		DECLARE @SenderDiv INT
		DECLARE @ReceiverDept INT
		DECLARE @ReceiverDiv INT

		SET @SenderDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Sender)

		SET @ReceiverDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		SET @SenderDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Sender)

		SET @ReceiverDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		IF (@Status IS NULL)
			SET @Status = 'New'

		if(@Comments IS NULL)
			SET @Comments = ''

		INSERT INTO DIMS_WORKITEM
				(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
				WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
				WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
				WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
				WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
				WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
				VALUES
				(@witmID, @WfID, @SentOnBehalf, @Recipient, @Type, @StepNo, 
				@Parent, @Instructions, GETDATE(), @DeadlineDate,
				@ReminderDate, @Status, @RootSender, @SenderDept,
				@SenderDiv, @ReceiverDept, @ReceiverDiv, @Status,
				@Sender, @SentOnBehalf, @Comments, 'ACTIVE', @SeqNo, @NodeNo)

		DECLARE @PrimaryDocID NVARCHAR(50)
		SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WfID)

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WfID, @Sender, GETDATE(), @WitmID, @Comments,@PrimaryDocID,@Status)

		IF (@OldID > 0)
		INSERT INTO DIMS_MAPPING.DBO.MAPPING (OLDID, NEWID, TYPE, SOURCE, STATUS)
		VALUES(@OldID, @WitmID, 'WORKITEM', 'NEW', 'ADDED')


		------------------ Logging all the db operations in the log db---------------------------
Declare @str nvarchar(max)
Declare @str1 nvarchar(max)

set @Str=CONCAT('INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
			WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
			VALUES (',NEWID(),',',@WfID,',',@Sender,',',GETDATE(),',',@WitmID,',','''Archived Item''',',',NULL,',','''Archive'')')

set @str1=CONCAT('INSERT INTO DIMS_WORKITEM
	(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
	WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
	WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
	WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
	WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
	WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
	VALUES
	(',@witmID,',',@WfID,',',@SentOnBehalf,',',@Recipient,',',@Type,',', @StepNo,',', 
	@Parent,',', @Instructions,',', GETDATE(),',', @DeadlineDate,',',
	@ReminderDate,',', @Status,',', @RootSender,',', @SenderDept,',',
	@SenderDiv,',', @ReceiverDept,',', @ReceiverDiv,',', @Status,',',
	@Sender,',', @SentOnBehalf,',', @Comments,',', '''ACTIVE''',',', @SeqNo,',', @NodeNo,')')

insert into SYNC_ERROR values (@WfID,@WitmID,@str1,concat('SYNC_DIMS_ADDUSER_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WfID,@WitmID,@str,concat('SYNC_DIMS_ADDUSER_WORKITEM ',getdate()))

--------------------------END of Logging-------------------------------------------------

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, @InWiID, 'ERROR', 'APP';
		THROW 503, 'Database error while adding user to work item', 1;
	END CATCH
			

	
END





GO
/****** Object:  StoredProcedure [dbo].[SYNC_DIMS_ARCHIVE_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SYNC_DIMS_ARCHIVE_WORKITEM]
@WitmID NVARCHAR(50),
@actionBy NVARCHAR(50),
@actionOnBehalf NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WflID NVARCHAR(50)
	DECLARE @wiStatus NVARCHAR(10)
	BEGIN TRY
		SELECT @WflID = WFL_ID FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID

		IF @actionOnBehalf IS NULL
			SET @actionOnBehalf = @actionBy

		UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS = 'Archive', WFL_WITM_ACTION = 'Archive',
			WFL_WITM_ACTION_BY = @actionBy, WFL_WITM_ACTION_ONBEHALF = @actionOnBehalf WHERE WFL_WITM_ID = @WitmID

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WflID, @actionBy, GETDATE(), @WitmID, 'Archived Item', NULL, 'Archive')


		------------------ Logging all the db operations in the log db---------------------------
Declare @str nvarchar(max)
Declare @str1 nvarchar(max)

set @Str=CONCAT('INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
			WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
			VALUES (',NEWID(),',',@WflID,',',@actionBy,',',GETDATE(),',',@WitmID,',','''Archived Item''',',',NULL,',','''Archive'')')

set @str1=CONCAT('UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS = ''Archive'', WFL_WITM_ACTION = ''Archive'',
		WFL_WITM_ACTION_BY =',@actionBy,',',' WFL_WITM_ACTION_ONBEHALF =', @actionOnBehalf,',','
		WFL_WITM_ACTION_COMMENT = ''Archived Item'' WHERE WFL_WITM_ID =',@WitmID)

insert into SYNC_ERROR values (@WflID,@WitmID,@str1,concat('SYNC_DIMS_ARCHIVE_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WflID,@WitmID,@str,concat('SYNC_DIMS_ARCHIVE_WORKITEM ',getdate()))

--------------------------END of Logging-------------------------------------------------

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 504, 'Database error while archiving work item', 1;
	END CATCH
END





GO
/****** Object:  StoredProcedure [dbo].[SYNC_DIMS_COMPLETE_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SYNC_DIMS_COMPLETE_WORKITEM]
@WitmID NVARCHAR(50),
@actionBy NVARCHAR(50),
@actionOnBehalf NVARCHAR(50) = null
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WflID NVARCHAR(50)
	BEGIN TRY
		IF(@actionOnBehalf IS NULL)
			SET @actionOnBehalf = @actionBy

		SELECT @WflID = WFL_ID FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID

		UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS='Complete', WFL_WITM_ACTION='Complete',
				WFL_WITM_ACTION_BY = @actionBy,  WFL_WITM_ACTION_ONBEHALF = @actionOnBehalf, 
				WFL_WITM_SYS_STATUS = 'INACTIVE'
				WHERE WFL_ID = @WflID

		UPDATE DIMS_WORKFLOW SET WFL_STATUS = 'INACTIVE', WFL_COMPLETEDBY = @actionBy,
				WFL_COMPLETEDONBEHALF = @actionOnBehalf, WFL_ENDDATE = GETDATE() 
				WHERE WFL_ID = @WflID

		DECLARE @PrimaryDocID NVARCHAR(50)
		SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WflID)

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WflID, @actionBy, GETDATE(), @WitmID, 'Completed Workflow',@PrimaryDocID,'Complete')

		------------------ Logging all the db operations in the log db---------------------------
Declare @str nvarchar(max)
Declare @str1 nvarchar(max)
Declare @str3 nvarchar(max)

set @Str=CONCAT('INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
			WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
			VALUES (',NEWID(),',',@WflID,',',@actionBy,',',GETDATE(),',',@WitmID,',','''Completed Workflow''',',',@PrimaryDocID,',','''Complete'')')

set @str1=CONCAT('UPDATE DIMS_WORKFLOW SET WFL_STATUS = ''INACTIVE'', WFL_COMPLETEDBY =',@actionBy,',',
			'WFL_COMPLETEDONBEHALF =', @actionOnBehalf,',',' WFL_ENDDATE = ',GETDATE(), '  
			WHERE WFL_ID = ',@WflID)

set @str3=CONCAT('UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS=''Complete'', WFL_WITM_ACTION=''Complete'',
			WFL_WITM_ACTION_BY = ',@actionBy,',','  WFL_WITM_ACTION_ONBEHALF =', @actionOnBehalf,',',' 
			WFL_WITM_SYS_STATUS = ''INACTIVE''
			WHERE WFL_ID =', @WflID)

insert into SYNC_ERROR values (@WflID,@WitmID,@str3,concat('SYNC_DIMS_COMPLETE_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WflID,@WitmID,@str1,concat('SYNC_DIMS_COMPLETE_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WflID,@WitmID,@str,concat('SYNC_DIMS_COMPLETE_WORKITEM ',getdate()))

--------------------------END of Logging-------------------------------------------------

	

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 505, 'Database error while completing work item', 1;
	END CATCH
			
END





GO
/****** Object:  StoredProcedure [dbo].[SYNC_DIMS_CREATE_WORKFLOW]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SYNC_DIMS_CREATE_WORKFLOW]
@Subject NVARCHAR(200),
@Deadline NVARCHAR(30) = NULL,
@Reminder NVARCHAR(30) = NULL,
@Priority INT = 1,
@LaunchedBY NVARCHAR(50),
@LaunchedOnBehalf NVARCHAR(50) = NULL,
@PrimaryDocID NVARCHAR(50),
@OLDID INT = 0
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	BEGIN TRY
	declare @WfID NVARCHAR(50)
		SET @WfID = NEWID()

		DECLARE @DeadlineDate DATETIME
		DECLARE @ReminderDate DATETIME

		IF (@Deadline IS NULL) OR (LEN(@Deadline) <= 0)
			SET @DeadlineDate = DATEADD(day,5,GETDATE())
		ELSE
			SET @DeadlineDate = DATEADD(minute,59,DATEADD(hour,23,convert(datetime,  @Deadline,103)))

		if (@Reminder IS NULL) OR (LEN(@Reminder) <= 0)
			SET @ReminderDate = DATEADD(day, -1, @DeadlineDate)
		ELSE
			SET @ReminderDate = DATEADD(minute,59,DATEADD(hour,23,convert(datetime,  @Reminder,103)))

		IF(@LaunchedOnBehalf IS NULL) OR (LEN(@LaunchedOnBehalf) <= 0)
			SET @LaunchedOnBehalf = @LaunchedBY

		DECLARE @WfName NVARCHAR(50)
		IF(LEN(@Subject) > 35)
			SET @WfName = 'Correspondence:' + LEFT(@Subject, 35)
		ELSE
			SET @WfName = 'Correspondence:' + @Subject

		IF (@Priority IS NULL) OR (@Priority < = 0) OR (@Priority > 3)
			SET @Priority = 1

		INSERT INTO DIMS_WORKFLOW
				(WFL_ID, WFL_NAME, WFL_SUBJECT, WFL_DEADLINE, WFL_REMINDER_DATE, WFL_PRIORITY,WFL_LAUNCHEDBY,WFL_LAUNCHEDONBEHALF,WFL_BEGINDATE,WFL_STATUS, WFL_PRIMARY_DOCUMENT)
				VALUES
				(@WfID, @WfName, @Subject, @DeadlineDate, @ReminderDate, @Priority, @LaunchedBY,@LaunchedOnBehalf,GETDATE(),'ACTIVE', @PrimaryDocID)

		IF (@OldID > 0)
		INSERT INTO DIMS_MAPPING.DBO.MAPPING (OLDID, NEWID, TYPE, SOURCE, STATUS)
		VALUES(@OldID, @WfID, 'WORKFLOW', 'NEW', 'ADDED')

				 ------------------ Logging all the db operations in the log db---------------------------
Declare @str nvarchar(max)


set @Str=CONCAT('INSERT INTO DIMS_WORKFLOW(WFL_ID, WFL_NAME, WFL_SUBJECT, WFL_DEADLINE, WFL_REMINDER_DATE, WFL_PRIORITY,WFL_LAUNCHEDBY,WFL_LAUNCHEDONBEHALF,WFL_BEGINDATE,WFL_STATUS, WFL_PRIMARY_DOCUMENT)
			VALUES (',@WfID,',', @WfName,',', @Subject,',', @DeadlineDate,',', @ReminderDate,',', @Priority,',', @LaunchedBY,',',@LaunchedOnBehalf,',',GETDATE(),',','''ACTIVE''',',', @PrimaryDocID,')')


insert into SYNC_ERROR values (@WfID,NULL,@str,concat('SYNC_DIMS_CREATE_WORKFLOW ',getdate()))

--------------------------END of Logging-------------------------------------------------

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, NULL, 'ERROR', 'APP';
		THROW 506, 'Database error while creating workflow', 1;
	END CATCH

END






GO
/****** Object:  StoredProcedure [dbo].[SYNC_DIMS_CREATE_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SYNC_DIMS_CREATE_WORKITEM]
@WfID NVARCHAR(50),
@Sender NVARCHAR(50),
@Recipient NVARCHAR(50),
@Type NVARCHAR(10),
@Parent NVARCHAR(50),
@Instructions NVARCHAR(255),
@Deadline NVARCHAR(30) = null,
@Reminder NVARCHAR(30) = null,
@SentOnBehalf NVARCHAR(50) = null,
@Status NVARCHAR(20) = null,
@Comments NVARCHAR(1024) = null,
@ActionBy NVARCHAR(50),
@OLDID INT = 0
AS
BEGIN
	SET NOCOUNT ON;
	BEGIN TRY

	declare @WitmID NVARCHAR(50)

		SET @WitmID = NEWID()

		DECLARE @DeadlineDate DATETIME
		DECLARE @ReminderDate DATETIME

		IF (@Deadline IS NULL) OR (LEN(@Deadline) <= 0)
			SET @DeadlineDate = DATEADD(day,5,GETDATE())
		ELSE
			SET @DeadlineDate = DATEADD(minute,59,DATEADD(hour,23,convert(datetime,  @Deadline,103)))

		if (@Reminder IS NULL) OR (LEN(@Reminder) <= 0)
			SET @ReminderDate = DATEADD(day, -1, @DeadlineDate)
		ELSE
			SET @ReminderDate = DATEADD(minute,59,DATEADD(hour,23,convert(datetime,  @Reminder,103)))

		IF(@SentOnBehalf IS NULL)
			SET @SentOnBehalf = @Sender

		DECLARE @StepNo INT = 1
		DECLARE @RootSender NVARCHAR(50) = @Sender
		DECLARE @SeqNo INT = 0
		DECLARE @NodeNo NVARCHAR(1024)
		IF(@Parent IS NOT NULL)
			BEGIN
				SET @StepNo = (SELECT TOP(1) WFL_STEP_NO FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent) + 1
				SET @RootSender = (SELECT TOP(1) WFL_WITM_ROOT_SENDER FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent)
				SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent) + 1
				SET @NodeNo = (SELECT TOP(1) WFL_WITM_NODE FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @Parent)
				SET @NodeNo = (SELECT CONCAT(@NodeNo, FORMAT(@SeqNo, '00#')))
			END 
		ELSE
			BEGIN
				SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM IS NULL AND WFL_ID = @WfID) + 1
				SET @NodeNo = (SELECT FORMAT(@SeqNo, '00#'))
			END

		DECLARE @SenderDept INT
		DECLARE @SenderDiv INT
		DECLARE @ReceiverDept INT
		DECLARE @ReceiverDiv INT

		SET @SenderDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Sender)

		SET @ReceiverDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		SET @SenderDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Sender)

		SET @ReceiverDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		IF (@Status IS NULL)
			SET @Status = 'New'
		if(@Comments IS NULL)
			SET @Comments = 'Launched'

		INSERT INTO DIMS_WORKITEM
				(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
				WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
				WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
				WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
				WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
				WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
				VALUES
				(@witmID, @WfID, @Sender, @Recipient, @Type, @StepNo, 
				@Parent, @Instructions, GETDATE(), @DeadlineDate,
				@ReminderDate, @Status, @RootSender, @SenderDept,
				@SenderDiv, @ReceiverDept, @ReceiverDiv, @Status,
				@ActionBy, @SentOnBehalf, @Comments, 'ACTIVE', @SeqNo, @NodeNo)

		DECLARE @PrimaryDocID NVARCHAR(50)
		SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WfID)

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WfID, @ActionBy, GETDATE(), @WitmID, @Comments,@PrimaryDocID,@Status)

		IF (@OldID > 0)
		INSERT INTO DIMS_MAPPING.DBO.MAPPING (OLDID, NEWID, TYPE, SOURCE, STATUS)
		VALUES(@OldID, @WitmID, 'WORKITEM', 'NEW', 'ADDED')

						
 ------------------ Logging all the db operations in the log db---------------------------
Declare @str nvarchar(max)
Declare @str1 nvarchar(max)

set @Str=CONCAT('INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
			WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
			VALUES (',NEWID(),',',@WfID,',',@ActionBy,',',GETDATE(),',',@WitmID,',',@Comments,',',@PrimaryDocID,',',@Status,')')

set @str1=CONCAT('INSERT INTO DIMS_WORKITEM
	(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
	WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
	WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
	WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
	WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
	WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
	VALUES
	(',@witmID,',',@WfID,',',@SentOnBehalf,',',@Recipient,',',@Type,',', @StepNo,',', 
	@Parent,',', @Instructions,',', GETDATE(),',', @DeadlineDate,',',
	@ReminderDate,',', @Status,',', @RootSender,',', @SenderDept,',',
	@SenderDiv,',', @ReceiverDept,',', @ReceiverDiv,',', @Status,',',
	@ActionBy,',', @SentOnBehalf,',', @Comments,',', '''ACTIVE''',',', @SeqNo,',', @NodeNo,')')

insert into SYNC_ERROR values (@WfID,@WitmID,@str1,concat('SYNC_DIMS_CREATE_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WfID,@WitmID,@str,concat('SYNC_DIMS_CREATE_WORKITEM ',getdate()))

--------------------------END of Logging-------------------------------------------------

	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, @WitmID, 'ERROR', 'APP'
	END CATCH


END





GO
/****** Object:  StoredProcedure [dbo].[SYNC_DIMS_DONE_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SYNC_DIMS_DONE_WORKITEM]
@WitmID NVARCHAR(50),
@actionBy NVARCHAR(50),
@actionComments NVARCHAR(50),
@actionOnBehalf NVARCHAR(50) = null,
@oldID Int = 0
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WflID NVARCHAR(50)
	DECLARE @Recipient NVARCHAR(50)
	DECLARE @NewWitmID NVARCHAR(50)
	DECLARE @DeadlineDate DATETIME
	DECLARE @ReminderDate DATETIME
	DECLARE @RootSender NVARCHAR(50)
	DECLARE @StepNo INT = 1
	DECLARE @ParentWitmID NVARCHAR(50)
	DECLARE @NodeNo NVARCHAR(1024)
	DECLARE @SeqNo INT = 1
	DECLARE @type NVARCHAR(20)

	BEGIN TRY
		IF(@actionOnBehalf IS NULL)
		SET @actionOnBehalf = @actionBy

	SELECT @WflID = WFL_ID, @DeadlineDate = WFL_WITM_DEADLINE, @Recipient = WFL_SENDER,
			@ReminderDate = WFL_WITM_REMINDER_DATE, @RootSender = WFL_WITM_ROOT_SENDER,
			@StepNo = WFL_STEP_NO, @NodeNo = WFL_WITM_NODE, @type = WFL_WITM_TYPE  
			FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID

	UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS='Done', WFL_WITM_ACTION='Done',
			WFL_WITM_ACTION_BY = @actionBy, WFL_WITM_ACTION_COMMENT = @actionComments,
			WFL_WITM_ACTION_ONBEHALF = @actionOnBehalf, WFL_WITM_SYS_STATUS = 'INACTIVE'
			WHERE WFL_WITM_ID = @WitmID

	DECLARE @wiRecipient NVARCHAR(50)
	DECLARE @oldNodeNo NVARCHAR(MAX)
	SET @wiRecipient = @Recipient
	SET @oldNodeNo = @NodeNo
	IF LEN(@NodeNo) > 3
		BEGIN
			SET @NodeNo = (SELECT LEFT(@NodeNo, LEN(@NodeNo) - 3))
			IF(SELECT COUNT(*) FROM DBO.DIMS_WORKITEM WHERE WFL_ID =  @WflID AND WFL_WITM_NODE = @oldNodeNo) > 1
			BEGIN			
				SET @Recipient = (SELECT WFL_RECIPIENT FROM DBO.DIMS_WORKITEM WHERE WFL_ID =  @WflID AND WFL_WITM_NODE = @NodeNo AND WFL_WITM_TYPE <> 'Reply')
				IF(@Recipient IS NULL)
					SET @Recipient = @wiRecipient
			END
			ELSE
				SET @Recipient = @wiRecipient
		END
	ELSE
		SET @Recipient = @RootSender

	IF(@type <> 'Reply')
		SET @Recipient = @wiRecipient

	SET @NewWitmID = NEWID()
	
	IF(@StepNo IS NOT NULL)
		SET @StepNo = @StepNo + 1
	if(@actionComments IS NULL)
		SET @actionComments = ''

	DECLARE @SenderDept INT
	DECLARE @SenderDiv INT
	DECLARE @ReceiverDept INT
	DECLARE @ReceiverDiv INT

	SET @SenderDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
						WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @actionOnBehalf)

	SET @ReceiverDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
						WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

	SET @SenderDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
						WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @actionOnBehalf)

	SET @ReceiverDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
						WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)
	EXEC dbo.APP_DIMS_INACTIVATE_CHILD_WORKITEMS @WitmID
	INSERT INTO DIMS_WORKITEM
			(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
			WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
			WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
			WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
			WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
			WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
			VALUES
			(@NewwitmID, @WflID, @actionOnBehalf, @Recipient, 'Reply', @StepNo, 
			@WitmID, 'Return', GETDATE(), @DeadlineDate,
			@ReminderDate, 'New', @RootSender, @SenderDept,
			@SenderDiv, @ReceiverDept, @ReceiverDiv, 'Done',
			@actionBy, @actionOnBehalf, @actionComments, 'ACTIVE', @SeqNo, @NodeNo)

	DECLARE @PrimaryDocID NVARCHAR(50)
	SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WflID)

	INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
			WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
			VALUES (NEWID(), @WflID, @actionBy, GETDATE(), @NewwitmID, @actionComments,@PrimaryDocID,'Done')

		IF (@OldID > 0)
		INSERT INTO DIMS_MAPPING.DBO.MAPPING (OLDID, NEWID, TYPE, SOURCE, STATUS)
		VALUES(@OldID, @NewwitmID, 'WORKITEM', 'NEW', 'ADDED')

				 ------------------ Logging all the db operations in the log db---------------------------
Declare @str nvarchar(max)
Declare @str1 nvarchar(max)
Declare @str3 nvarchar(max)

set @Str=CONCAT('INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
			WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
			VALUES (',NEWID(),',',@WflID,',',@actionBy,',',GETDATE(),',',@NewWitmID,',',@actionComments,',',@PrimaryDocID,',','''Done'')')

set @str1=CONCAT('INSERT INTO DIMS_WORKITEM
	(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
	WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
	WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
	WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
	WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
	WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
	VALUES
	(',@NewwitmID,',',@WflID,',',@actionOnBehalf,',',@Recipient,',','''Reply''',',', @StepNo,',', 
	@WitmID,',', '''Return''',',', GETDATE(),',', @DeadlineDate,',',
	@ReminderDate,',', '''New''',',', @RootSender,',', @SenderDept,',',
	@SenderDiv,',', @ReceiverDept,',', @ReceiverDiv,',', '''Done''',',',
	@actionBy,',', @actionOnBehalf,',', @actionComments,',', '''ACTIVE''',',', @SeqNo,',', @NodeNo,')')

set @str3=CONCAT('UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS=''Done'', WFL_WITM_ACTION=''Done'',
			WFL_WITM_ACTION_BY = ',@actionBy,',','  WFL_WITM_ACTION_ONBEHALF =', @actionOnBehalf,',',' 
			WFL_WITM_ACTION_COMMENT =', @actionComments,',','
			WFL_WITM_SYS_STATUS = ''INACTIVE''
			WHERE WFL_WITM_ID = ', @WitmID)

insert into SYNC_ERROR values (@WflID,@WitmID,@str3,concat('SYNC_DIMS_DONE_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WflID,@WitmID,@str1,concat('SYNC_DIMS_DONE_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WflID,@WitmID,@str,concat('SYNC_DIMS_DONE_WORKITEM ',getdate()))

--------------------------END of Logging-------------------------------------------------



	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP'
	END CATCH
		
END





GO
/****** Object:  StoredProcedure [dbo].[SYNC_DIMS_FORWARD_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SYNC_DIMS_FORWARD_WORKITEM]
@WitmID NVARCHAR(50),
@actionBy NVARCHAR(50),
@actionComments NVARCHAR(50),
@actionOnBehalf NVARCHAR(50) = null,
@Recipient NVARCHAR(50),
@Type NVARCHAR(10),
@Instructions NVARCHAR(255),
@Deadline NVARCHAR(30) = null,
@Reminder NVARCHAR(30) = null,
@Iterator INT = 0,
@OLDID INT = 0
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WflID NVARCHAR(50)
	DECLARE @NewWitmID NVARCHAR(50)
	DECLARE @DeadlineDate DATETIME
	DECLARE @ReminderDate DATETIME
	DECLARE @RootSender NVARCHAR(50)
	DECLARE @StepNo INT = 1
	DECLARE @NodeNo NVARCHAR(1024)
	DECLARE @SeqNo INT = 0
	BEGIN TRY
		IF(@actionOnBehalf IS NULL)
			SET @actionOnBehalf = @actionBy

		SELECT @WflID = WFL_ID, @RootSender = WFL_WITM_ROOT_SENDER, @StepNo = WFL_STEP_NO, @NodeNo = WFL_WITM_NODE 
				FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @WitmID

		SET @SeqNo = (SELECT COUNT(WFL_WITM_ID) FROM DBO.DIMS_WORKITEM WHERE WFL_PARENT_WITM = @WitmID) + 1
		SET @NodeNo = (SELECT CONCAT(@NodeNo, FORMAT(@SeqNo, '00#')))

		IF @Iterator <= 0
			UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS='Forward', WFL_WITM_ACTION='Forward',
				WFL_WITM_ACTION_BY = @actionBy, 
				WFL_WITM_ACTION_ONBEHALF = @actionOnBehalf, WFL_WITM_SYS_STATUS = 'INACTIVE'
				WHERE WFL_WITM_ID = @WitmID

		SET @NewWitmID = NEWID()
	
		IF (@Deadline IS NULL) OR (LEN(@Deadline) <= 0)
			SET @DeadlineDate = DATEADD(day,5,GETDATE())
		ELSE
			SET @DeadlineDate = convert(datetime, @Deadline,103)

		if (@Reminder IS NULL) OR (LEN(@Reminder) <= 0)
			SET @ReminderDate = DATEADD(day, -1, @DeadlineDate)
		ELSE
			SET @ReminderDate = convert(datetime, @Reminder,103)

		IF(@StepNo IS NOT NULL)
			SET @StepNo = @StepNo + 1
		IF(@actionComments IS NULL)
			SET @actionComments = ''

		DECLARE @SenderDept INT
		DECLARE @SenderDiv INT
		DECLARE @ReceiverDept INT
		DECLARE @ReceiverDiv INT

		SET @SenderDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @actionOnBehalf)

		SET @ReceiverDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		SET @SenderDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @actionOnBehalf)

		SET @ReceiverDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		INSERT INTO DIMS_WORKITEM
				(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
				WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
				WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
				WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
				WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
				WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
				VALUES
				(@NewwitmID, @WflID, @actionOnBehalf, @Recipient, @Type, @StepNo, 
				@WitmID, @Instructions, GETDATE(), @DeadlineDate,
				@ReminderDate, 'New', @RootSender, @SenderDept,
				@SenderDiv, @ReceiverDept, @ReceiverDiv, 'Forward',
				@actionBy, @actionOnBehalf, @actionComments, 'ACTIVE', @SeqNo, @NodeNo)

		DECLARE @PrimaryDocID NVARCHAR(50)
		SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WflID)

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WflID, @actionBy, GETDATE(), @NewwitmID, @actionComments,@PrimaryDocID,'Forward')

				DECLARE @WFL_PRIORITY1 AS INT
				SELECT       @WFL_PRIORITY1=[WFL_PRIORITY]        FROM [DIMS].[dbo].[DIMS_WORKFLOW] WHERE WFL_ID=@WflID

		IF (@OldID > 0)
        INSERT INTO DIMS_MAPPING.DBO.MAPPING (OLDID, NEWID, TYPE, SOURCE, STATUS)
        VALUES(@OldID, @NewwitmID, 'WORKITEM', 'NEW', 'ADDED')

		 ------------------ Logging all the db operations in the log db---------------------------
Declare @str nvarchar(max)
Declare @str1 nvarchar(max)
Declare @str3 nvarchar(max)

set @Str=CONCAT('INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
			WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
			VALUES (',NEWID(),',',@WflID,',',@actionBy,',',GETDATE(),',',@NewWitmID,',',@actionComments,',',@PrimaryDocID,',','''Forward'')')

set @str1=CONCAT('INSERT INTO DIMS_WORKITEM
	(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
	WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
	WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
	WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
	WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
	WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
	VALUES
	(',@NewwitmID,',',@WflID,',',@actionOnBehalf,',',@Recipient,',',@Type,',', @StepNo,',', 
	@WitmID,',', @Instructions,',', GETDATE(),',', @DeadlineDate,',',
	@ReminderDate,',', '''New''',',', @RootSender,',', @SenderDept,',',
	@SenderDiv,',', @ReceiverDept,',', @ReceiverDiv,',', '''Forward''',',',
	@actionBy,',', @actionOnBehalf,',', @actionComments,',', '''ACTIVE''',',', @SeqNo,',', @NodeNo,')')

set @str3=CONCAT('UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS=''Forward'', WFL_WITM_ACTION=''Forward'',
			WFL_WITM_ACTION_BY = ',@actionBy,',','  WFL_WITM_ACTION_ONBEHALF =', @actionOnBehalf,',',' 
			WFL_WITM_ACTION_COMMENT =', @actionComments,',','
			WFL_WITM_SYS_STATUS = ''INACTIVE''
			WHERE WFL_WITM_ID = ', @WitmID)

insert into SYNC_ERROR values (@WflID,@WitmID,@str3,concat('SYNC_DIMS_FORWARD_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WflID,@WitmID,@str1,concat('SYNC_DIMS_FORWARD_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WflID,@WitmID,@str,concat('SYNC_DIMS_FORWARD_WORKITEM ',getdate()))

--------------------------END of Logging-------------------------------------------------




	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 508, 'Database error while forwarding work item', 1;
	END CATCH

END





GO
/****** Object:  StoredProcedure [dbo].[SYNC_DIMS_READ_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SYNC_DIMS_READ_WORKITEM]
@WitmID NVARCHAR(50),
@userLogin NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @Recipient NVARCHAR(50)
	DECLARE @WflID NVARCHAR(50)
	DECLARE @wiStatus NVARCHAR(10)
	BEGIN TRY
		SELECT @wiStatus = WFL_WITM_STATUS, @Recipient = WFL_RECIPIENT, @WflID = WFL_ID FROM DIMS_WORKITEM
		WHERE WFL_WITM_ID = @WitmID

		IF (@userLogin IS NULL)
			SET @userLogin = @Recipient

		IF(@wiStatus = 'New')
		BEGIN
			UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS = 'Read', WFL_WITM_ACTION = 'Read',
				WFL_WITM_ACTION_BY = @userLogin, WFL_WITM_ACTION_ONBEHALF = @Recipient WHERE WFL_WITM_ID = @WitmID

			INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
					WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
					VALUES (NEWID(), @WflID, @userLogin, GETDATE(), @WitmID, 'Read', NULL, 'Read')
		END
	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WflID, @WitmID, 'ERROR', 'APP';
		THROW 510, 'Database error while reading work item', 1;
	END CATCH
END





GO
/****** Object:  StoredProcedure [dbo].[SYNC_DIMS_REASSIGN_WORKITEM]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SYNC_DIMS_REASSIGN_WORKITEM]
@InWiID NVARCHAR(50),
@Recipient NVARCHAR(50),
@actionBy NVARCHAR(50),
@actionOnBehalf NVARCHAR(50) = null,
@actionComments NVARCHAR(255),
@originalRecipient NVARCHAR(50),
@OldID Int = 0
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @WfID NVARCHAR(50)
	DECLARE @WitmID NVARCHAR(50)
	DECLARE @ExtWitm NVARCHAR(50)
	DECLARE @DeadlineDate DATETIME
	DECLARE @ReminderDate DATETIME
	DECLARE @StepNo INT
	DECLARE @RootSender NVARCHAR(50)
	DECLARE @Type NVARCHAR(10)
	DECLARE @Instructions NVARCHAR(255)
	DECLARE @NodeNo NVARCHAR(1024)
	DECLARE @origWitmID NVARCHAR(50)
	DECLARE @SeqNo INT = 0
	DECLARE @NewNode NVARCHAR(1024)
	
	DECLARE @originalSender NVARCHAR(50)

	BEGIN TRY
		SELECT @WfID = WFL_ID, @originalSender = WFL_SENDER FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @InWiID
		SET @origWitmID = (SELECT TOP(1) WFL_WITM_ID FROM DIMS_WORKITEM WHERE WFL_ID = @WfID AND
					WFL_RECIPIENT = @originalRecipient AND WFL_WITM_TYPE = 'to')
		IF(@origWitmID IS NULL)
			SET @origWitmID = @InWiID

		SELECT  @DeadlineDate = WFL_WITM_DEADLINE, @ReminderDate = WFL_WITM_REMINDER_DATE, 
				@StepNo = WFL_STEP_NO, @RootSender = WFL_WITM_ROOT_SENDER, @Type = WFL_WITM_TYPE, 
				@Instructions = WFL_INSTRUCTIONS, @NodeNo = WFL_WITM_NODE, @SeqNo = WFL_WITM_SEQNO,
				@NewNode = WFL_WITM_NODE
		FROM DIMS_WORKITEM WHERE WFL_WITM_ID = @origWitmID

		UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS='Reassign', WFL_WITM_ACTION='Reassign',
				WFL_WITM_ACTION_BY = @actionBy,  WFL_WITM_ACTION_ONBEHALF = @actionOnBehalf, 
				WFL_WITM_ACTION_COMMENT = @actionComments, WFL_WITM_SYS_STATUS = 'INACTIVE',
				WFL_WITM_SEQNO = 999, WFL_WITM_NODE = '999'
				WHERE WFL_WITM_ID = @origWitmID

		SET @WitmID = NEWID()

		IF(@actionOnBehalf IS NULL)
			SET @actionOnBehalf = @actionBy

		DECLARE @SenderDept INT
		DECLARE @SenderDiv INT
		DECLARE @ReceiverDept INT
		DECLARE @ReceiverDiv INT

		SET @SenderDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @originalSender)

		SET @ReceiverDept = (SELECT TOP(1) ECM_DEPT_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		SET @SenderDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @originalSender)

		SET @ReceiverDiv = (SELECT TOP(1) ECM_DIVISION_CODE FROM ECM_EMPLOYEE
							WHERE ECM_EMPLOYEE.ECM_USER_LOGIN = @Recipient)

		INSERT INTO DIMS_WORKITEM
				(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
				WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
				WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
				WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
				WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
				WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
				VALUES
				(@witmID, @WfID, @originalSender, @Recipient, @Type, @StepNo, 
				@origWitmID, @Instructions, GETDATE(), @DeadlineDate,
				@ReminderDate, 'New', @RootSender, @SenderDept,
				@SenderDiv, @ReceiverDept, @ReceiverDiv, 'Reassign',
				@actionBy, @actionOnBehalf, @actionComments, 'ACTIVE',
				@SeqNo, @NewNode)

		DECLARE @PrimaryDocID NVARCHAR(50)
		SET @PrimaryDocID = (SELECT TOP(1) WFL_PRIMARY_DOCUMENT FROM DBO.DIMS_WORKFLOW WHERE DBO.DIMS_WORKFLOW.WFL_ID = @WfID)

		INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
				WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
				VALUES (NEWID(), @WfID, @actionBy, GETDATE(), @WitmID, 'Reassigned',@PrimaryDocID,'Reassign')

		IF (@OldID > 0)
		INSERT INTO DIMS_MAPPING.DBO.MAPPING (OLDID, NEWID, TYPE, SOURCE, STATUS)
		VALUES(@OldID, @WitmID, 'WORKITEM', 'NEW', 'ADDED')

		 ------------------ Logging all the db operations in the log db---------------------------
Declare @str nvarchar(max)
Declare @str1 nvarchar(max)
Declare @str3 nvarchar(max)

set @Str=CONCAT('INSERT INTO DIMS_WORKFLOW_HISTORY(WFL_HISTORY_ID, WFL_ID, WFL_ACTION_USER, WFL_ACTION_TIMESTAMP,
			WFL_ACTION_WITM_ID, WFL_ACTION_DETAILS, WFL_ACTION_DOCUMENT, WFL_ACTION_STATUS)
			VALUES (',NEWID(),',',@WfID,',',@actionBy,',',GETDATE(),',',@WitmID,',','''Reassigned''',',',@PrimaryDocID,',','''Reassign'')')

set @str1=CONCAT('INSERT INTO DIMS_WORKITEM
	(WFL_WITM_ID, WFL_ID, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_TYPE, WFL_STEP_NO, 
	WFL_PARENT_WITM, WFL_INSTRUCTIONS,WFL_WITM_RECEIVEDON,WFL_WITM_DEADLINE, 
	WFL_WITM_REMINDER_DATE, WFL_WITM_STATUS, WFL_WITM_ROOT_SENDER, WFL_WITM_SENDER_DEPT,
	WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DEPT, WFL_WITM_RECEIVER_DIV,
	WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_ACTION_ONBEHALF, WFL_WITM_ACTION_COMMENT, 
	WFL_WITM_SYS_STATUS, WFL_WITM_SEQNO, WFL_WITM_NODE )
	VALUES
	(',@witmID,',',@WfID,',',@actionOnBehalf,',',@Recipient,',',@Type,',', @StepNo,',', 
	@origWitmID,',', @Instructions,',', GETDATE(),',', @DeadlineDate,',',
	@ReminderDate,',', '''New''',',', @RootSender,',', @SenderDept,',',
	@SenderDiv,',', @ReceiverDept,',', @ReceiverDiv,',', '''Reassign''',',',
	@actionBy,',', @actionOnBehalf,',', @actionComments,',', '''ACTIVE''',',', @SeqNo,',', @NodeNo,')')

set @str3=CONCAT('UPDATE DIMS_WORKITEM SET WFL_WITM_STATUS=''Reassign'', WFL_WITM_ACTION=''Reassign'',
			WFL_WITM_ACTION_BY = ',@actionBy,',','  WFL_WITM_ACTION_ONBEHALF =', @actionOnBehalf,',',' 
			WFL_WITM_ACTION_COMMENT =', @actionComments,',',',
            WFL_WITM_SEQNO = 999, WFL_WITM_NODE = ''999'',
			WFL_WITM_SYS_STATUS = ''INACTIVE''
			WHERE WFL_WITM_ID = ', @origWitmID)

insert into SYNC_ERROR values (@WfID,@WitmID,@str3,concat('SYNC_DIMS_REASSIGN_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WfID,@WitmID,@str1,concat('SYNC_DIMS_REASSIGN_WORKITEM ',getdate()))
insert into SYNC_ERROR values (@WfID,@WitmID,@str,concat('SYNC_DIMS_REASSIGN_WORKITEM ',getdate()))

--------------------------END of Logging-------------------------------------------------



	END TRY
	BEGIN CATCH
		EXECUTE DIMS_LOG_ERROR @WfID, @WitmID, 'ERROR', 'APP';
		THROW 511, 'Database error while reassigning work item', 1;
	END CATCH
END


GO
/****** Object:  UserDefinedFunction [dbo].[ISPARENTOF]    Script Date: 10/23/2017 10:38:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[ISPARENTOF](
@parent NVARCHAR(50),
@child NVARCHAR(50)
)
RETURNS INT
BEGIN

	IF(@parent = @child)
		return 1;

	DECLARE @retValue INT = 0;
	DECLARE @parentWi NVARCHAR(50)
	SET @parentWi = (SELECT TOP(1) WFL_PARENT_WITM FROM dbo.DIMS_WORKITEM
		WHERE WFL_WITM_ID = @child)

	WHILE (@parentWi IS NOT NULL)
	BEGIN
		IF(@parentWi = @parent)
		BEGIN
			SET @parentWi = NULL;
			SET @retValue = 1;
		END
		ELSE
			SET @parentWi = (SELECT TOP(1) WFL_PARENT_WITM FROM dbo.DIMS_WORKITEM
				WHERE WFL_WITM_ID = @parentWi)
	END

	RETURN @retValue;
END
GO
