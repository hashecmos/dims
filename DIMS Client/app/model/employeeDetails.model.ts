import {EmployeeDivision} from './employeeDivision.model'
import {EmployeeDirectorate} from './employeeDirectorate.model'
import {EmployeeDepartment} from './employeeDepartment.model'

export class EmployeeDetails {
  employeeID: number
  employeeName: string
  employeeLogin: string
  admin: boolean
  employeeDirectorate: EmployeeDirectorate
  employeeDepartment: EmployeeDepartment
  employeeDivision: EmployeeDivision
  isHaveInbox: number;
  isHaveReports: number;
}
