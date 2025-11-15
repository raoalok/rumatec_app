package com.js_loop_erp.components

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.js_loop_erp.components.data_class.LoginResponsePanav
import com.js_loop_erp.components.data_class.Permission
import com.js_loop_erp.components.data_class.User
import com.js_loop_erp.components.fragments.EmpLocationData
import androidx.core.database.sqlite.transaction

class ApplicationDb(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null,DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {

        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_SALES_PERSON_ID INTEGER, " +
                "$COLUMN_LATITUDE TEXT, " +
                "$COLUMN_LONGITUDE TEXT, " +
                "$COLUMN_ET TEXT, " +
                "$COLUMN_ALTITUDE TEXT, " +
                "$COLUMN_VELOCITY TEXT, " +
                "$COLUMN_HORIZONTAL_ACCURACY TEXT, " +
                "$COLUMN_VERTICAL_ACCURACY TEXT, " +
                "$COLUMN_REMARK TEXT, " +
                "$COLUMN_DATE TEXT)"
        db?.execSQL(createTableQuery)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    fun insertSalesData(data: EmpLocationData) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_SALES_PERSON_ID, data.salesPersonId)
        contentValues.put(COLUMN_LATITUDE, data.lat)
        contentValues.put(COLUMN_LONGITUDE, data.long)
        contentValues.put(COLUMN_ET, data.et)
        contentValues.put(COLUMN_ALTITUDE, data.alt)
        contentValues.put(COLUMN_VELOCITY, data.vel)
        contentValues.put(COLUMN_HORIZONTAL_ACCURACY, data.hAcc)
        contentValues.put(COLUMN_VERTICAL_ACCURACY, data.vAcc)
        contentValues.put(COLUMN_REMARK, data.remark)
        contentValues.put(COLUMN_DATE, data.date)
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
        Log.d(TAG, "insertSalesData: EmpLocationData:: ${data}")
    }


    fun getTotalEntries(): Int {
        val db = this.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(countQuery, null)
        var totalCount = 0
        if (cursor.moveToFirst()) {
            totalCount = cursor.getInt(0)
        }
        cursor.close()
        return totalCount
    }

    fun getLatestEntries(): List<EmpLocationData> {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY _id DESC LIMIT 10"
        val cursor: Cursor? = db.rawQuery(selectQuery, null)
        val entriesList = mutableListOf<EmpLocationData>()



        if (cursor != null && cursor.moveToFirst()) {
            val salesPersonIdIndex = cursor.getColumnIndex("salesPersonId")
            val latIndex = cursor.getColumnIndex("lat")
            val longIndex = cursor.getColumnIndex("long")
            val etIndex = cursor.getColumnIndex("et")
            val altIndex = cursor.getColumnIndex("alt")
            val velIndex = cursor.getColumnIndex("vel")
            val hAccIndex = cursor.getColumnIndex("hAcc")
            val vAccIndex = cursor.getColumnIndex("vAcc")
            val remarkIndex = cursor.getColumnIndex("remark")
            val dateIndex = cursor.getColumnIndex("date")
            do {
                val salesData = EmpLocationData(
                    cursor.getInt(salesPersonIdIndex),
                    cursor.getString(latIndex) ?: "",
                    cursor.getString(longIndex) ?: "",
                    cursor.getString(etIndex) ?: "",
                    cursor.getString(altIndex) ?: "",
                    cursor.getString(velIndex) ?: "",
                    cursor.getString(hAccIndex) ?: "",
                    cursor.getString(vAccIndex) ?: "",
                    cursor.getString(remarkIndex) ?: "",
                    cursor.getString(dateIndex) ?: ""
                )
                entriesList.add(salesData)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return entriesList
    }

    fun getLastEntries(number: Int): List<EmpLocationData> {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY _id ASC LIMIT $number"
        val cursor: Cursor? = db.rawQuery(selectQuery, null)
        val entriesList = mutableListOf<EmpLocationData>()



        if (cursor != null && cursor.moveToFirst()) {
            val salesPersonIdIndex = cursor.getColumnIndex("salesPersonId")
            val latIndex = cursor.getColumnIndex("lat")
            val longIndex = cursor.getColumnIndex("long")
            val etIndex = cursor.getColumnIndex("et")
            val altIndex = cursor.getColumnIndex("alt")
            val velIndex = cursor.getColumnIndex("vel")
            val hAccIndex = cursor.getColumnIndex("hAcc")
            val vAccIndex = cursor.getColumnIndex("vAcc")
            val remarkIndex = cursor.getColumnIndex("remark")
            val dateIndex = cursor.getColumnIndex("date")
            do {
                val salesData = EmpLocationData(
                    cursor.getInt(salesPersonIdIndex),
                    cursor.getString(latIndex) ?: "",
                    cursor.getString(longIndex) ?: "",
                    cursor.getString(etIndex) ?: "",
                    cursor.getString(altIndex) ?: "",
                    cursor.getString(velIndex) ?: "",
                    cursor.getString(hAccIndex) ?: "",
                    cursor.getString(vAccIndex) ?: "",
                    cursor.getString(remarkIndex) ?: "",
                    cursor.getString(dateIndex) ?: ""
                )
                entriesList.add(salesData)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return entriesList
    }


    fun deleteTopEntries() {
        val db = this.writableDatabase
        db.transaction() {
            try {
                val deleteQuery =
                    "DELETE FROM $TABLE_NAME WHERE _id IN (SELECT _id FROM $TABLE_NAME ORDER BY _id DESC LIMIT 10)"
                execSQL(deleteQuery)
            } finally {
            }
        }
    }

    fun deleteLastEntries(number: Int) {
        val db = this.writableDatabase
        db.transaction() {
            try {
                val deleteQuery =
                    "DELETE FROM $TABLE_NAME WHERE _id IN (SELECT _id FROM $TABLE_NAME ORDER BY _id ASC LIMIT $number)"
                execSQL(deleteQuery)
            } finally {
            }
        }
    }

    fun insertLoginData(data: LoginResponsePanav) {
        val db = this.writableDatabase

        // Insert user data
        data.user?.let { user ->
            val userContentValues = ContentValues().apply {
                put(COLUMN_USER_ID, user.id)
                put(COLUMN_NAME, user.name)
                put(COLUMN_CONTACT, user.contact)
                put(COLUMN_ADDRESS, user.address)
                put(COLUMN_ROLE_ID, user.roleId)
                put(COLUMN_DESIGNATION, user.designation)
                put(COLUMN_DEPARTMENT, user.department)
                put(COLUMN_ROLE, user.role)
            }
            db.insert(TABLE_USER, null, userContentValues)
        }

        // Insert permissions data
        data.userPermissions?.let { permissions ->
            val permissionsContentValues = ContentValues().apply {
                put(COLUMN_GET_USERS, permissions.getUsers)
                put(COLUMN_ADD_USER, permissions.addUser)
                put(COLUMN_EDIT_USER, permissions.editUser)
                put(COLUMN_DELETE_USER, permissions.deleteUser)
                put(COLUMN_GET_ROLES,permissions.getRoles)
                put(COLUMN_ADD_ROLES,permissions.addRole)
                put(COLUMN_EDIT_ROLES,permissions.editRole)
                put(COLUMN_DELETE_ROLE,permissions.deleteRole)
                put(COLUMN_GET_DEPARTMENT,permissions.getDepartments)
                put(COLUMN_ADD_DEPARTMENT,permissions.addDepartment)
                put(COLUMN_EDIT_DEPARTMENT,permissions.editDepartment)
                put(COLUMN_DELETE_DEPARTMENT,permissions.deleteDepartment)
                put(COLUMN_GET_DESIGNATION,permissions.getDesignations)
                put(COLUMN_ADD_DESIGNATION,permissions.addDesignation)
                put(COLUMN_EDIT_DESIGNATION,permissions.editDesignation)
                put(COLUMN_DELETE_DESIGNATION,permissions.deleteDesignation)
                put(COLUMN_GET_CONSIGNORS,permissions.getConsignors)
                put(COLUMN_ADD_CONSIGNORS,permissions.addConsignor)
                put(COLUMN_EDIT_CONSIGNORS,permissions.editConsignor)
                put(COLUMN_DELETE_CONSIGNERS,permissions.deleteConsignor)
                put(COLUMN_GET_HSNS,permissions.getHsns)
                put(COLUMN_ADD_HSN,permissions.addHsn)
                put(COLUMN_EDIT_HSN,permissions.editHsn)
                put(COLUMN_DELETE_HSN,permissions.deleteHsn)
                put(COLUMN_GET_UNIT,permissions.getUnits)
                put(COLUMN_ADD_UNIT,permissions.addUnit)
                put(COLUMN_EDIT_UNIT,permissions.editUnit)
                put(COLUMN_DELETE_UNIT,permissions.deleteUnit)
                put(COLUMN_GET_CHARGES,permissions.getCharges)
                put(COLUMN_ADD_CHARGES,permissions.addCharge)
                put(COLUMN_EDIT_CHARGES,permissions.editCharge)
                put(COLUMN_DELETE_CHARGES,permissions.deleteCharge)
                put(COLUMN_GET_PARTIES,permissions.getParties)
                put(COLUMN_ADD_PARTIES,permissions.addParty)
                put(COLUMN_EDIT_PARTIES,permissions.editParty)
                put(COLUMN_DELETE_PARTIES,permissions.deleteParty)
                put(COLUMN_VERIFY_PARTIES,permissions.verifyParty)
                put(COLUMN_VIEW_PARTY_LEDGER,permissions.viewPartyLedger)
                put(COLUMN_GET_PRODUCT,permissions.getProducts)
                put(COLUMN_ADD_PRODUCT,permissions.addProduct)
                put(COLUMN_EDIT_PRODUCT,permissions.editProduct)
                put(COLUMN_DELETE_PRODUCT,permissions.deleteProduct)
                put(COLUMN_RESTORE_PRODUCT,permissions.restoreProduct)
                put(COLUMN_VIEW_PRODUCT_LEDGER,permissions.viewProductLedger)
                put(COLUMN_GET_BANKS,permissions.getBanks)
                put(COLUMN_ADD_BANK,permissions.addBank)
                put(COLUMN_EDIT_BANK,permissions.editBank)
                put(COLUMN_DELETE_DELETE,permissions.deleteBank)
                put(COLUMN_GET_CONTACTS,permissions.getContacts)
                put(COLUMN_ADD_CONTACTS,permissions.addContact)
                put(COLUMN_EDIT_CONTACTS,permissions.editContact)
                put(COLUMN_DELETE_CONTACTS,permissions.deleteContact)
                put(COLUMN_GET_NARRATIONS,permissions.getNarrations)
                put(COLUMN_ADD_NARRATIONS,permissions.addNarration)
                put(COLUMN_EDIT_NARATTIONS,permissions.editNarration)
                put(COLUMN_DELETE_NARATTIONS,permissions.deleteNarration)
                put(COLUMN_GET_TRANSECTIONS,permissions.getTransactions)
                put(COLUMN_ADD_TRANSECTION,permissions.addTransaction)
                put(COLUMN_EDIT_TRANSECTION,permissions.editTransaction)
                put(COLUMN_DELETE_TRANSECTION,permissions.deleteTransaction)

                put(COLUMN_FINANCE_REPORTS,permissions.financeReports)
                put(COLUMN_YEARLY_SALES_FIGURE,permissions.yealySaleFigures)
                put(COLUMN_YEARLY_PURCHASE_FIGURES,permissions.yealyPurchaseFigures)
                put(COLUMN_ON_TIME_RECEIVE_REPORT,permissions.onTimeReceivedReport)
                put(COLUMN_GET_LOCATIONS,permissions.getLocations)
                put(COLUMN_GET_LOG_TRACKING_FILES,permissions.getLogTrackingFiles)
                put(COLUMN_GET_ROUTES,permissions.getRoutes)
                put(COLUMN_ADD_ROUTE,permissions.addRoute)
                put(COLUMN_EDIT_ROUTE,permissions.editRoute)
                put(COLUMN_DELETE_ROUTE,permissions.deleteRoute)
                put(COLUMN_GET_AREAS,permissions.getAreas)
                put(COLUMN_ADD_AREA,permissions.addArea)
                put(COLUMN_EDIT_AREA,permissions.editArea)
                put(COLUMN_DELETE_AREA,permissions.deleteArea)
                put(COLUMN_GET_PET_SHOPS,permissions.getPetShops)
                put(COLUMN_ADD_PET_SHOP,permissions.addPetShop)
                put(COLUMN_EDIT_PET_SHOP,permissions.editPetShop)
                put(COLUMN_DELETE_PET_SHOP,permissions.deletePetShop)
                put(COLUMN_GET_HOSPITAL,permissions.getHospitals)
                put(COLUMN_ADD_HOSPITAL,permissions.addHospital)
                put(COLUMN_EDIT_HOSPITAL,permissions.editHospital)
                put(COLUMN_DELETE_HOSPITAL,permissions.deleteHospital)
                put(COLUMN_GET_INSTITUTES,permissions.getInstitutes)
                put(COLUMN_ADD_INSTITUTES,permissions.addInstitute)
                put(COLUMN_EDIT_INSTITUTES,permissions.editInstitute)
                put(COLUMN_DELETE_INSTITUTES,permissions.deleteInstitute)
                put(COLUMN_GET_DOCTORS,permissions.getDoctors)
                put(COLUMN_ADD_DOCTORS,permissions.addDoctor)
                put(COLUMN_EDIT_DOCTORS,permissions.editDoctor)
                put(COLUMN_DELETE_DOCTORS,permissions.deleteDoctor)
                put(COLUMN_GET_TOURS,permissions.getTours)
                put(COLUMN_ADD_TOURS,permissions.addTour)
                put(COLUMN_EDIT_TOURS,permissions.editTour)
                put(COLUMN_DELETE_TOURS,permissions.deleteTour)
                put(COLUMN_GET_AUTHORIZE_TOURS,permissions.authorizeTours)
                put(COLUMN_GET_EXPENSES,permissions.getExpenses)
                put(COLUMN_ADD_EXPENSE,permissions.addExpense)
                put(COLUMN_EDIT_EXPENSE,permissions.editExpense)
                put(COLUMN_DELETE_EXPENSE,permissions.deleteExpense)
                put(COLUMN_GET_AUTHORIZE_EXPENSE,permissions.authorizeExpenses)
                put(COLUMN_GET_HEADQUARTERS,permissions.getHeadquarters)
                put(COLUMN_ADD_HEADQUARTERS,permissions.addHeadquarter)
                put(COLUMN_EDIT_HEADQUARTERS,permissions.editHeadquarter)
                put(COLUMN_DELETE_HEADQUARTERS,permissions.deleteHeadquarter)
                put(COLUMN_GET_OFFERS,permissions.getOffers)
                put(COLUMN_ADD_OFFERS,permissions.addOffer)
                put(COLUMN_DELETE_OFFERS,permissions.deleteOffer)
                put(COLUMN_GET_LINKS,permissions.getLinks)
                put(COLUMN_ADD_LINKS,permissions.addLink)
                put(COLUMN_DELETE_LINKS,permissions.deleteLink)
                put(COLUMN_GET_PRICE_LIST,permissions.getPriceList)
                put(COLUMN_ADD_PRICE_LIST,permissions.addPriceList)
                put(COLUMN_EDIT_PRICE_LIST,permissions.editPriceList)
                put(COLUMN_DELETE_PRICE_LIST,permissions.deletePriceList)
                put(COLUMN_GET_VIEW_INVENTORY_RATE,permissions.viewInventoryRate)
                put(COLUMN_GET_PARTIES_CLOSING_STOCKS,permissions.getPartiesClosingStocks)
                put(COLUMN_ADD_PARTIES_CLOSING_STOCKS,permissions.addPartiesClosingStock)
                put(COLUMN_EDIT_PARTIES_CLOSING_STOCKS,permissions.editPartiesClosingStock)
                put(COLUMN_DELETE_PARTIES_CLOSING_STOCKS,permissions.deletePartiesClosingStock)

                //put(,permissions.)
                // Continue for all other permissions...
            }
            db.insert(TABLE_PERMISSIONS, null, permissionsContentValues)
        }

        // Insert token data
        val tokenContentValues = ContentValues().apply {
            put(COLUMN_TOKEN, data.token)
        }
        db.insert(TABLE_TOKEN, null, tokenContentValues)

        db.close()
        Log.d(TAG, "insertApiResponseData: ApiResponse:: $data")
    }


    fun getLoginData(): LoginResponsePanav? {
        val db = this.readableDatabase

        var user: User? = null
        var permissions: Permission? = null
        var token: String? = null

        // Read user data
        val userCursor = db.query(TABLE_USER, null, null, null, null, null, null)
        if (userCursor.moveToFirst()) {
            user = User(
                id = userCursor.getInt(userCursor.getColumnIndexOrThrow("id")),
                name = userCursor.getString(userCursor.getColumnIndexOrThrow("name")),
                contact = userCursor.getString(userCursor.getColumnIndexOrThrow("contact")),
                address = userCursor.getString(userCursor.getColumnIndexOrThrow("address")),
                roleId = userCursor.getInt(userCursor.getColumnIndexOrThrow("roleId")),
                designation = userCursor.getString(userCursor.getColumnIndexOrThrow("designation")),
                department = userCursor.getString(userCursor.getColumnIndexOrThrow("department")),
                role = userCursor.getString(userCursor.getColumnIndexOrThrow("role"))
            )
        }
        userCursor.close()

        // Read token data
        val tokenCursor = db.query(TABLE_TOKEN, null, null, null, null, null, null)
        if (tokenCursor.moveToFirst()) {
            token = tokenCursor.getString(tokenCursor.getColumnIndexOrThrow("token"))
        }
        tokenCursor.close()

        // Read permissions data
        val permissionsCursor = db.query(TABLE_PERMISSIONS, null, null, null, null, null, null)
        if (permissionsCursor.moveToFirst()) {
            permissions = Permission(
                getUsers = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetUsers")) == 1,
                addUser = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddUser")) == 1,
                editUser = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditUser")) == 1,
                deleteUser = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteUser")) == 1,
                getRoles = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetRoles")) == 1,
                addRole = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddRole")) == 1,
                editRole = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditRole")) == 1,
                deleteRole = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteRole")) == 1,
                getDepartments = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetDepartments")) == 1,
                addDepartment = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddDepartment")) == 1,
                editDepartment = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditDepartment")) == 1,
                deleteDepartment = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteDepartment")) == 1,
                getDesignations = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetDesignations")) == 1,
                addDesignation = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddDesignation")) == 1,
                editDesignation = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditDesignation")) == 1,
                deleteDesignation = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteDesignation")) == 1,
                getConsignors = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetConsignors")) == 1,
                addConsignor = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddConsignor")) == 1,
                editConsignor = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditConsignor")) == 1,
                deleteConsignor = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteConsignor")) == 1,
                getHsns = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetHsns")) == 1,
                addHsn = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddHsn")) == 1,
                editHsn = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditHsn")) == 1,
                deleteHsn = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteHsn")) == 1,
                getUnits = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetUnits")) == 1,
                addUnit = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddUnit")) == 1,
                editUnit = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditUnit")) == 1,
                deleteUnit = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteUnit")) == 1,
                getCharges = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetCharges")) == 1,
                addCharge = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddCharge")) == 1,
                editCharge = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditCharge")) == 1,
                deleteCharge = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteCharge")) == 1,
                getParties = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetParties")) == 1,
                addParty = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddParty")) == 1,
                editParty = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditParty")) == 1,
                deleteParty = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteParty")) == 1,
                verifyParty = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("VerifyParty")) == 1,
                viewPartyLedger = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("ViewPartyLedger")) == 1,
                getProducts = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetProducts")) == 1,
                addProduct = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddProduct")) == 1,
                editProduct = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditProduct")) == 1,
                deleteProduct = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteProduct")) == 1,
                restoreProduct = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("RestoreProduct")) == 1,
                viewProductLedger = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("ViewProductLedger")) == 1,
                getBanks = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetBanks")) == 1,
                addBank = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddBank")) == 1,
                editBank = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditBank")) == 1,
                deleteBank = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteBank")) == 1,
                getContacts = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetContacts")) == 1,
                addContact = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddContact")) == 1,
                editContact = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditContact")) == 1,
                deleteContact = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteContact")) == 1,
                getNarrations = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetNarrations")) == 1,
                addNarration = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddNarration")) == 1,
                editNarration = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditNarration")) == 1,
                deleteNarration = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteNarration")) == 1,
                getTransactions = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetTransactions")) == 1,
                addTransaction = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddTransaction")) == 1,
                editTransaction = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditTransaction")) == 1,
                deleteTransaction = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteTransaction")) == 1,

                financeReports = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("FinanceReports")) == 1,
                yealySaleFigures = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("YearlySalesFigures")) == 1,
                yealyPurchaseFigures = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("YearlyPurchaseFigures")) == 1,
                onTimeReceivedReport = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("OnTimeReceiveReport")) == 1,
                getLocations = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetLocations")) == 1,
                getLogTrackingFiles = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetLogTrackingFiles")) == 1,
                getRoutes = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetRoutes")) == 1,
                addRoute = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddRoute")) == 1,
                editRoute = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditRoute")) == 1,
                deleteRoute = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteRoute")) == 1,
                getAreas = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetAreas")) == 1,
                addArea = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddArea")) == 1,
                editArea = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditArea")) == 1,
                deleteArea = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteArea")) == 1,
                getPetShops = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetPetShops")) == 1,
                addPetShop = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddPetShop")) == 1,
                editPetShop = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditPetShop")) == 1,
                deletePetShop = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeletePetShop")) == 1,
                getHospitals = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetHospitals")) == 1,
                addHospital = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddHospital")) == 1,
                editHospital = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditHospital")) == 1,
                deleteHospital = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteHospital")) == 1,
                getInstitutes = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetInstitutes")) == 1,
                addInstitute = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddInstitute")) == 1,
                editInstitute = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditInstitute")) == 1,
                deleteInstitute = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteInstitute")) == 1,
                getDoctors = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetDoctors")) == 1,
                addDoctor = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddDoctor")) == 1,
                editDoctor = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditDoctor")) == 1,
                deleteDoctor = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteDoctor")) == 1,
                getTours = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetTours")) == 1,
                addTour = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddTour")) == 1,
                editTour = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditTour")) == 1,
                deleteTour = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteTour")) == 1,
                authorizeTours = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AuthorizeTours")) == 1,
                getExpenses = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetExpenses")) == 1,
                addExpense = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddExpense")) == 1,
                editExpense = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditExpense")) == 1,
                deleteExpense = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteExpense")) == 1,
                authorizeExpenses = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AuthorizeExpenses")) == 1,
                getHeadquarters = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetHeadquarters")) == 1,
                addHeadquarter = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddHeadquarter")) == 1,
                editHeadquarter = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditHeadquarter")) == 1,
                deleteHeadquarter = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteHeadquarter")) == 1,
                getOffers = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetOffers")) == 1,
                addOffer = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddOffer")) == 1,
                deleteOffer = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteOffer")) == 1,
                getLinks = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetLinks")) == 1,
                addLink = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddLink")) == 1,
                deleteLink = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeleteLink")) == 1,
                getPriceList = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetPriceList")) == 1,
                addPriceList = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddPriceList")) == 1,
                editPriceList = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditPriceList")) == 1,
                deletePriceList = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeletePriceList")) == 1,
                viewInventoryRate = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("ViewInventoryRate")) == 1,
                getPartiesClosingStocks = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("GetPartiesClosingStocks")) == 1,
                addPartiesClosingStock = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("AddPartiesClosingStock")) == 1,
                editPartiesClosingStock = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("EditPartiesClosingStock")) == 1,
                deletePartiesClosingStock = permissionsCursor.getInt(permissionsCursor.getColumnIndexOrThrow("DeletePartiesClosingStock")) == 1

                // Add other permissions similarly...
            )
        }
        permissionsCursor.close()

        db.close()

        return if (user != null && permissions != null && token != null) {
            LoginResponsePanav(
                user = user,
                userPermissions = permissions,
                token = token
            )
        } else {
            null
        }
    }

    fun deleteLoginData(userId: Int) {
        val db = this.writableDatabase

        // Delete user data
        val userSelection = "$COLUMN_USER_ID = ?"
        val userSelectionArgs = arrayOf(userId.toString())
        db.delete(TABLE_USER, userSelection, userSelectionArgs)

        // Delete permissions data
        val permissionsSelection = "$COLUMN_USER_ID = ?"
        db.delete(TABLE_PERMISSIONS, permissionsSelection, userSelectionArgs)

        // Delete token data
        val tokenSelection = "$COLUMN_USER_ID = ?"
        db.delete(TABLE_TOKEN, tokenSelection, userSelectionArgs)

        db.close()
        Log.d(TAG, "deleteLoginData: Deleted data for userId:: $userId")
    }


    companion object {

        val TAG = ApplicationDb::class.java.name

        private const val DATABASE_NAME = "my_database.db"
        private const val DATABASE_VERSION = 1

        // Define your table and columns
        const val TABLE_NAME = "sales_data"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_SALES_PERSON_ID = "salesPersonId"
        private const val COLUMN_LATITUDE = "lat"
        private const val COLUMN_LONGITUDE = "long"
        private const val COLUMN_ET = "et"
        private const val COLUMN_ALTITUDE = "alt"
        private const val COLUMN_VELOCITY = "vel"
        private const val COLUMN_HORIZONTAL_ACCURACY = "hAcc"
        private const val COLUMN_VERTICAL_ACCURACY = "vAcc"
        private const val COLUMN_REMARK = "remark"
        private const val COLUMN_DATE = "date"

        // Assuming you have defined constants for column names and table names like:
        const val TABLE_USER = "UserTable"
        const val TABLE_PERMISSIONS = "PermissionsTable"
        const val TABLE_TOKEN = "TokenTable"
        const val COLUMN_USER_ID = "UserId"
        const val COLUMN_NAME = "Name"
        const val COLUMN_CONTACT = "Contact"
        const val COLUMN_ADDRESS = "Address"
        const val COLUMN_ROLE_ID = "RoleId"
        const val COLUMN_DESIGNATION = "Designation"
        const val COLUMN_DEPARTMENT = "Department"
        const val COLUMN_ROLE = "Role"
        const val COLUMN_TOKEN = "Token"
        const val COLUMN_GET_USERS = "GetUsers"
        const val COLUMN_ADD_USER = "AddUser"
        const val COLUMN_EDIT_USER = "EditUser"
        const val COLUMN_DELETE_USER = "DeleteUser"
        const val COLUMN_GET_ROLES = "GetRoles"
        const val COLUMN_ADD_ROLES = "AddRoles"
        const val COLUMN_EDIT_ROLES = "EditRoles"
        const val COLUMN_DELETE_ROLE = "DeleteRole"
        const val COLUMN_GET_DEPARTMENT = "GetDepartments"
        const val COLUMN_ADD_DEPARTMENT = "AddDepartments"
        const val COLUMN_EDIT_DEPARTMENT = "EditDepartments"
        const val COLUMN_DELETE_DEPARTMENT = "DeleteDepartment"
        const val COLUMN_GET_DESIGNATION = "GetDesignation"
        const val COLUMN_ADD_DESIGNATION = "AddDesignation"
        const val COLUMN_EDIT_DESIGNATION = "EditDesignation"
        const val COLUMN_DELETE_DESIGNATION = "DeleteDesignation"
        const val COLUMN_GET_CONSIGNORS = "GetConsignors"
        const val COLUMN_ADD_CONSIGNORS = "AddConsignors"
        const val COLUMN_EDIT_CONSIGNORS = "EditConsignors"
        const val COLUMN_DELETE_CONSIGNERS = "DeleteConsigners"
        const val COLUMN_GET_HSNS = "GetHsns"
        const val COLUMN_ADD_HSN = "AddHsn"
        const val COLUMN_EDIT_HSN = "EditHsn"
        const val COLUMN_DELETE_HSN = "DeleteHsn"
        const val COLUMN_GET_UNIT = "GetUnit"
        const val COLUMN_ADD_UNIT = "AddUnit"
        const val COLUMN_EDIT_UNIT = "EditUnit"
        const val COLUMN_DELETE_UNIT = "DeleteUnit"
        const val COLUMN_GET_CHARGES =""
        const val COLUMN_ADD_CHARGES =""
        const val COLUMN_EDIT_CHARGES =""
        const val COLUMN_DELETE_CHARGES =""
        const val COLUMN_GET_PARTIES = ""
        const val COLUMN_ADD_PARTIES = ""
        const val COLUMN_EDIT_PARTIES = ""
        const val COLUMN_DELETE_PARTIES = ""
        const val COLUMN_VERIFY_PARTIES = ""
        const val COLUMN_VIEW_PARTY_LEDGER = ""
        const val COLUMN_GET_PRODUCT =""
        const val COLUMN_ADD_PRODUCT =""
        const val COLUMN_EDIT_PRODUCT =""
        const val COLUMN_DELETE_PRODUCT =""
        const val COLUMN_RESTORE_PRODUCT =""
        const val COLUMN_VIEW_PRODUCT_LEDGER =""
        const val COLUMN_GET_BANKS =""
        const val COLUMN_ADD_BANK =""
        const val COLUMN_EDIT_BANK =""
        const val COLUMN_DELETE_DELETE =""
        const val COLUMN_GET_CONTACTS =""
        const val COLUMN_ADD_CONTACTS =""
        const val COLUMN_EDIT_CONTACTS =""
        const val COLUMN_DELETE_CONTACTS =""
        const val COLUMN_GET_NARRATIONS =""
        const val COLUMN_ADD_NARRATIONS =""
        const val COLUMN_EDIT_NARATTIONS =""
        const val COLUMN_DELETE_NARATTIONS =""
        const val COLUMN_GET_TRANSECTIONS =""
        const val COLUMN_ADD_TRANSECTION =""
        const val COLUMN_EDIT_TRANSECTION =""
        const val COLUMN_DELETE_TRANSECTION =""

        const val COLUMN_FINANCE_REPORTS = ""
        const val COLUMN_YEARLY_SALES_FIGURE = ""
        const val COLUMN_YEARLY_PURCHASE_FIGURES = ""
        const val COLUMN_ON_TIME_RECEIVE_REPORT = ""
        const val COLUMN_GET_LOCATIONS = ""
        const val COLUMN_GET_LOG_TRACKING_FILES = ""
        const val COLUMN_GET_ROUTES =""
        const val COLUMN_ADD_ROUTE =""
        const val COLUMN_EDIT_ROUTE =""
        const val COLUMN_DELETE_ROUTE =""
        const val COLUMN_GET_AREAS =""
        const val COLUMN_ADD_AREA =""
        const val COLUMN_EDIT_AREA =""
        const val COLUMN_DELETE_AREA =""
        const val COLUMN_GET_PET_SHOPS =""
        const val COLUMN_ADD_PET_SHOP =""
        const val COLUMN_EDIT_PET_SHOP =""
        const val COLUMN_DELETE_PET_SHOP = ""
        const val COLUMN_GET_HOSPITAL =""
        const val COLUMN_ADD_HOSPITAL =""
        const val COLUMN_EDIT_HOSPITAL =""
        const val COLUMN_DELETE_HOSPITAL =""
        const val COLUMN_GET_INSTITUTES =""
        const val COLUMN_ADD_INSTITUTES =""
        const val COLUMN_EDIT_INSTITUTES =""
        const val COLUMN_DELETE_INSTITUTES =""
        const val COLUMN_GET_DOCTORS =""
        const val COLUMN_ADD_DOCTORS =""
        const val COLUMN_EDIT_DOCTORS =""
        const val COLUMN_DELETE_DOCTORS =""
        const val COLUMN_GET_TOURS =""
        const val COLUMN_ADD_TOURS =""
        const val COLUMN_EDIT_TOURS =""
        const val COLUMN_DELETE_TOURS =""
        const val COLUMN_GET_AUTHORIZE_TOURS = ""
        const val COLUMN_GET_EXPENSES =""
        const val COLUMN_ADD_EXPENSE =""
        const val COLUMN_EDIT_EXPENSE =""
        const val COLUMN_DELETE_EXPENSE =""
        const val COLUMN_GET_AUTHORIZE_EXPENSE =""
        const val COLUMN_GET_HEADQUARTERS =""
        const val COLUMN_ADD_HEADQUARTERS =""
        const val COLUMN_EDIT_HEADQUARTERS =""
        const val COLUMN_DELETE_HEADQUARTERS=""
        const val COLUMN_GET_OFFERS =""
        const val COLUMN_ADD_OFFERS =""
        const val COLUMN_EDIT_OFFERS =""
        const val COLUMN_DELETE_OFFERS =""
        const val COLUMN_GET_LINKS =""
        const val COLUMN_ADD_LINKS =""
        const val COLUMN_EDIT_LINKS =""
        const val COLUMN_DELETE_LINKS =""
        const val COLUMN_GET_PRICE_LIST =""
        const val COLUMN_ADD_PRICE_LIST =""
        const val COLUMN_EDIT_PRICE_LIST =""
        const val COLUMN_DELETE_PRICE_LIST =""
        const val COLUMN_GET_VIEW_INVENTORY_RATE =""
        const val COLUMN_GET_PARTIES_CLOSING_STOCKS =""
        const val COLUMN_ADD_PARTIES_CLOSING_STOCKS =""
        const val COLUMN_EDIT_PARTIES_CLOSING_STOCKS =""
        const val COLUMN_DELETE_PARTIES_CLOSING_STOCKS =""
            /*const val COLUMN_GET_ =""
            const val COLUMN_GET_ =*/


        /*        const val COLUMN_GET_ =""
                const val COLUMN_ADD_ =""
                const val COLUMN_EDIT_ =""
                const val COLUMN_DELETE_ =""*/
    }

}

/*data class EmpLocationData(
    val salesPersonId: Int,
    val lat: String,
    val long: String,
    val et: String,
    val alt: String,
    val vel: String,
    val hAcc: String,
    val vAcc: String,
    val remark: String,
    val date: String
)*/


/*
interface DBOperations {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: EmpLocationData)

    @Query("SELECT * FROM ${ApplicationDb.TABLE_NAME}")
    suspend fun getData(): List<EmpLocationData>
}*/
