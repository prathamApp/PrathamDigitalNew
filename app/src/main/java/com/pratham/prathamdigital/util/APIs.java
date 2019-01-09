package com.pratham.prathamdigital.util;

public class APIs {
    private APIs() {
    }

    public static final String village = "village";
    public static final String CRL = "KOLIBRI_CRL";
    public static final String Group = "Groups";
    public static final String Student = "Student";

    /*  public static final String HL = "Hybrid Learning";
      public static final String HLpullVillagesKolibriURL = "http://www.hlearning.openiscool.org/api/village/get?programId=1&state=";
      public static final String HLpullGroupsKolibriURL = "http://www.devtab.openiscool.org/api/Group?programid=1&villageId=";
      public static final String HLpullStudentsKolibriURL = "http://www.devtab.openiscool.org/api/student?programid=1&villageId=";
      public static final String HLpullCrlsKolibriURL = "http://www.swap.prathamcms.org/api/UserList?programId=1&statecode=";


      public static final String ECE = "ECE";
      public static final String ECEpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=8&state=";
      public static final String ECEpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=8&villageId=";
      public static final String ECEpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=8&villageId=";
      public static final String ECEpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=8&statecode=";

  */
    public static final String UP = "Urban programme";
    public static final String UPpullVillagesKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=village&filter_name=programid:6,state:";
    public static final String UPpullVillagesServerURL = "http://www.hlearning.openiscool.org/api/village/get?programId=6&state=";
    public static final String UPpullGroupsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=group&filter_name=programid:6,villageid:";
    public static final String UPpullGroupsServerURL = "http://www.devtab.openiscool.org/api/Group?programid=6&villageId=";
    public static final String UPpullStudentsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=student&filter_name=programid:6,villageid:";
    public static final String UPpullStudentsServerURL = "http://www.devtab.openiscool.org/api/student?programid=6&villageId=";
    public static final String UPpullCrlsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=Crl&filter_name=programid:6,state:";
    public static final String UPpullCrlsServerURL = "http://www.swap.prathamcms.org/api/UserList?programId=6&statecode=";


    public static final String HL = "Hybrid Learning";
    public static final String HLpullVillagesKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=village&filter_name=programid:1,state:";
    public static final String HLpullVillagesServerURL = "http://www.hlearning.openiscool.org/api/village/get?programId=1&state=";
    public static final String HLpullGroupsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=group&filter_name=programid:1,villageid:";
    public static final String HLpullGroupsServerURL = "http://www.devtab.openiscool.org/api/Group?programid=1&villageId=";
    public static final String HLpullStudentsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=student&filter_name=programid:1,villageid:";
    public static final String HLpullStudentsServerURL = "http://www.devtab.openiscool.org/api/student?programid=1&villageId=";
    public static final String HLpullCrlsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=Crl&filter_name=programid:1,state:";
    public static final String HLpullCrlsServerURL = "http://www.swap.prathamcms.org/api/UserList?programId=1&statecode=";


    public static final String RI = "Read India";
    public static final String RIpullVillagesKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=village&filter_name=programid:2,state:";
    public static final String RIpullVillagesServerURL = "http://www.hlearning.openiscool.org/api/village/get?programId=2&state=";
    public static final String RIpullGroupsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=group&filter_name=programid:2,villageid:";
    public static final String RIpullGroupsServerURL = "http://www.devtab.openiscool.org/api/Group?programid=2&villageId=";
    public static final String RIpullStudentsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=student&filter_name=programid:2,villageid:";
    public static final String RIpullStudentsServerURL = "http://www.devtab.openiscool.org/api/student?programid=2&villageId=";
    public static final String RIpullCrlsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=Crl&filter_name=programid:2,state:";
    public static final String RIpullCrlsServerURL = "http://www.readindia.openiscool.org/api/crl/get?programId=2";

    public static final String SC = "Second Chance";
    public static final String SCpullVillagesKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=village&filter_name=programid:3,state:";
    public static final String SCpullVillagesServerURL = "http://www.hlearning.openiscool.org/api/village/get?programId=3&state=";
    public static final String SCpullGroupsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=group&filter_name=programid:3,villageid:";
    public static final String SCpullGroupsServerURL = "http://www.devtab.openiscool.org/api/Group?programid=3&villageId=";
    public static final String SCpullStudentsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=student&filter_name=programid:3,villageid:";
    public static final String SCpullStudentsServerURL = "http://www.devtab.openiscool.org/api/student?programid=3&villageId=";
    public static final String SCpullCrlsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=Crl&filter_name=programid:3,state:";
    public static final String SCpullCrlsServerURL = "http://www.swap.prathamcms.org/api/UserList?programId=3&statecode=";

    public static final String PI = "Pratham Institute";
    public static final String PIpullVillagesKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=village&filter_name=programid:4,state:";
    public static final String PIpullVillagesServerURL = "http://www.hlearning.openiscool.org/api/village/get?programId=4&state=";
    public static final String PIpullGroupsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=group&filter_name=programid:4,villageid:";
    public static final String PIpullGroupsServerURL = "http://www.devtab.openiscool.org/api/Group?programid=4&villageId=";
    public static final String PIpullStudentsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=student&filter_name=programid:4,villageid:";
    public static final String PIpullStudentsServerURL = "http://www.devtab.openiscool.org/api/student?programid=4&villageId=";
    public static final String PIpullCrlsKolibriURL = "http://192.168.4.1:8080/pratham/datastore/?table_name=Crl&filter_name=programid:4,state:";
    public static final String PIpullCrlsServerURL = "http://www.tabdata.prathaminstitute.org/api/crl/get?programId=4:";


    //NewPushURL
    /*  public static final String HLpushToServerURL = "http://www.hlearning.openiscool.org/api/datapush/pushusage";*/
    public static final String HLpushToServerURL = "http://www.swap.prathamcms.org/api/QRSwap/SwapData";
    public static final String RIpushToServerURL = "http://www.readindia.openiscool.org/api/datapush/pushusage";
    public static final String SCpushToServerURL = "http://www.hlearning.openiscool.org/api/datapush/pushusage";
    public static final String PIpushToServerURL = "http://www.tabdata.prathaminstitute.org/api/datapush/pushusage";

    // Device Tracking Push API
    public static final String TabTrackPushAPI = "http://www.swap.prathamcms.org/api/QRPush/QRPushData";

    // Pull Courses
    public static final String PullCourses = "http://www.swap.prathamcms.org/api/course/get";

    // Pull HLCourseCommunity
    // public static final String PullHLCourseCommunity = "http://swap.prathamcms.org/api/HLCoach/GetHLCourseCommunity/?villageid=1&programid=1";
    public static final String PullHLCourseCommunity = "http://swap.prathamcms.org/api/HLCoach/GetHLCourseCommunity/?";

    // Pull HLCourseCompletion
    // public static final String PullHLCourseCompletion = "http://swap.prathamcms.org/api/HLCoach/GetHLCourseCompletion/?villageid=1&programid=1";
    public static final String PullHLCourseCompletion = "http://swap.prathamcms.org/api/HLCoach/GetHLCourseCompletion/?";

    // Pull Coaches
    // public static final String PullCoaches = "http://swap.prathamcms.org/api/HLCoach/GetHLCoachInfo/?villageid=1&programid=1";
    public static final String PullCoaches = "http://swap.prathamcms.org/api/HLCoach/GetHLCoachInfo/?";

    // Push API of Forms
    public static final String PushForms = "http://www.swap.prathamcms.org/api/crlvisit/crlvisitpushdata";

    // Assign/ Return Admin App API
    public static final String AssignReturn = "http://swap.prathamcms.org/api/AssignReturn/pushdata";

    // Device List API
    public static final String DeviceList = "http://swap.prathamcms.org/api/tablist?userid=";

    //Store person AOI
    public static final String storePersonAPI = "http://www.swap.prathamcms.org/api/Vendor?programId=17&statecode=";


}
