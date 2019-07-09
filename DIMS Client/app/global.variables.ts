'use strict';

export var base_url: string = 'http://ecmdemo1:9080/DIMS/resources';
export var navigator_url: string = 'http://ecmdemo1:9080/navigator';
export var getUserList: string = 'http://ecmdemo1:9080/DIMS/resources/EmployeeService/getEmailIds?email=:keyword';
export var viewer_url : string ='http://vmsrefdimsfv:9080/WorkplaceXT/iviewpro/WcmJavaViewer.jsp?'
export var os_name ='ECM';

// ----- Use the below block for  ----  KNPC Build
//export var base_url: string = 'http://vmsrefdimsfce:9080/DIMSSrv/resources';
//export var navigator_url: string = 'http://vmsrefdimsicn:9080/navigator';
//export var getUserList: string = 'http://vmsrefdimsfce:9080/DIMSSrv/resources/EmployeeService/getEmailIds?email=:keyword';
//export var viewer_url : string ='http://vmsrefdimsfv:9080/WorkplaceXT/iviewpro/WcmJavaViewer.jsp?'
//export var os_name ='DIMS'; 
// End KNPC build block


// -----   Use the below block for ----   KNPC PRODUCTION ENV
//export var base_url: string = 'https://dims2.knpc.net:9443/DIMSSrv/resources';
//export var navigator_url: string = 'https://dims2.knpc.net:9443/navigator';
//export var getUserList: string = 'https://dims2.knpc.net:9443/DIMSSrv/resources/EmployeeService/getEmailIds?email=:keyword';
//export var viewer_url : string ='http://dimsviewer.knpc.net:9080/WorkplaceXT/iviewpro/WcmJavaViewer.jsp?'
//export var os_name ='DIMS'; 
// End KNPC build block


export var dateObj = new Date();
dateObj.setDate(dateObj.getDate() - 1)
export var years=dateObj.getFullYear();
export var months=dateObj.getMonth()+1; 
export var days=dateObj.getDate();
export var date_picker_options = {
  dateFormat: 'dd/mm/yyyy',
  showTodayBtn: false,
  editableDateField: false,
  markCurrentDay: true,
  showClearDateBtn: false,
  openSelectorOnInputClick: true
};
export var date_picker_options_disabledpast = {
  dateFormat: 'dd/mm/yyyy',
  showTodayBtn: false,
  editableDateField: false,
  markCurrentDay: true,
  showClearDateBtn: false,
  openSelectorOnInputClick: true,
  openSelectorTopOfInput:true,
  disableUntil: {year:years, month:months, day:days},
};

export var date_picker_disabled = {
  dateFormat: 'dd/mm/yyyy',
  showTodayBtn: false,
  editableDateField: true,
  markCurrentDay: true,
  showClearDateBtn: false,
  openSelectorOnInputClick: true,
  componentDisabled: true,
};

export var date_picker_options_disable = {
  dateFormat: 'dd/mm/yyyy',
  showTodayBtn: false,
  editableDateField: false,
  markCurrentDay: true,
  showClearDateBtn: false,
  openSelectorOnInputClick: true,
  disableSince: {year:years, month:months, day:new Date().getDate()+1}
};


export var date_picker_disabled_Reports = {
  dateFormat: 'dd/mm/yyyy',
  showTodayBtn: false,
  editableDateField: false,
  markCurrentDay: true,
  showClearDateBtn: false,
  openSelectorOnInputClick: true,
  componentDisabled: true,
};

export var date_picker_options_disable_Reports = {
  dateFormat: 'dd/mm/yyyy',
  showTodayBtn: false,
  editableDateField: false,
  markCurrentDay: true,
  showClearDateBtn: false,
  openSelectorOnInputClick: true,
  disableSince: {year:new Date().getFullYear(), month:(new Date().getMonth()+1), day:new Date().getDate()+1}
};

export var date_range_option = {
      dateFormat: 'dd/mm/yyyy',
      showTodayBtn: false,
      editableDateField: false,
      markCurrentDay: true,
      showClearDateBtn: false,
      openSelectorOnInputClick: true,
      indicateInvalidDateRange: true
    };
