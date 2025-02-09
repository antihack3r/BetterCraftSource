/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.platform.win32;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.DsGetDC;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.LMAccess;
import com.sun.jna.platform.win32.Netapi32;
import com.sun.jna.platform.win32.Ole32Util;
import com.sun.jna.platform.win32.Secur32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;

public abstract class Netapi32Util {
    public static String getDCName() {
        return Netapi32Util.getDCName(null, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getDCName(String serverName, String domainName) {
        PointerByReference bufptr = new PointerByReference();
        try {
            int rc2 = Netapi32.INSTANCE.NetGetDCName(domainName, serverName, bufptr);
            if (0 != rc2) {
                throw new Win32Exception(rc2);
            }
            String string = bufptr.getValue().getString(0L, true);
            return string;
        }
        finally {
            if (0 != Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue())) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        }
    }

    public static int getJoinStatus() {
        return Netapi32Util.getJoinStatus(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int getJoinStatus(String computerName) {
        PointerByReference lpNameBuffer = new PointerByReference();
        IntByReference bufferType = new IntByReference();
        try {
            int rc2 = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
            if (0 != rc2) {
                throw new Win32Exception(rc2);
            }
            int n2 = bufferType.getValue();
            return n2;
        }
        finally {
            int rc3;
            if (lpNameBuffer.getPointer() != null && 0 != (rc3 = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue()))) {
                throw new Win32Exception(rc3);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getDomainName(String computerName) {
        PointerByReference lpNameBuffer = new PointerByReference();
        IntByReference bufferType = new IntByReference();
        try {
            int rc2 = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
            if (0 != rc2) {
                throw new Win32Exception(rc2);
            }
            String string = lpNameBuffer.getValue().getString(0L, true);
            return string;
        }
        finally {
            int rc3;
            if (lpNameBuffer.getPointer() != null && 0 != (rc3 = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue()))) {
                throw new Win32Exception(rc3);
            }
        }
    }

    public static LocalGroup[] getLocalGroups() {
        return Netapi32Util.getLocalGroups(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static LocalGroup[] getLocalGroups(String serverName) {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesRead = new IntByReference();
        IntByReference totalEntries = new IntByReference();
        try {
            int rc2 = Netapi32.INSTANCE.NetLocalGroupEnum(serverName, 1, bufptr, -1, entriesRead, totalEntries, null);
            if (0 != rc2 || bufptr.getValue() == Pointer.NULL) {
                throw new Win32Exception(rc2);
            }
            LMAccess.LOCALGROUP_INFO_1 group = new LMAccess.LOCALGROUP_INFO_1(bufptr.getValue());
            LMAccess.LOCALGROUP_INFO_1[] groups = (LMAccess.LOCALGROUP_INFO_1[])group.toArray(entriesRead.getValue());
            ArrayList<LocalGroup> result = new ArrayList<LocalGroup>();
            for (LMAccess.LOCALGROUP_INFO_1 lgpi : groups) {
                LocalGroup lgp = new LocalGroup();
                lgp.name = lgpi.lgrui1_name.toString();
                lgp.comment = lgpi.lgrui1_comment.toString();
                result.add(lgp);
            }
            LocalGroup[] localGroupArray = result.toArray(new LocalGroup[0]);
            return localGroupArray;
        }
        finally {
            int rc3;
            if (bufptr.getValue() != Pointer.NULL && 0 != (rc3 = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                throw new Win32Exception(rc3);
            }
        }
    }

    public static Group[] getGlobalGroups() {
        return Netapi32Util.getGlobalGroups(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Group[] getGlobalGroups(String serverName) {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesRead = new IntByReference();
        IntByReference totalEntries = new IntByReference();
        try {
            int rc2 = Netapi32.INSTANCE.NetGroupEnum(serverName, 1, bufptr, -1, entriesRead, totalEntries, null);
            if (0 != rc2 || bufptr.getValue() == Pointer.NULL) {
                throw new Win32Exception(rc2);
            }
            LMAccess.GROUP_INFO_1 group = new LMAccess.GROUP_INFO_1(bufptr.getValue());
            LMAccess.GROUP_INFO_1[] groups = (LMAccess.GROUP_INFO_1[])group.toArray(entriesRead.getValue());
            ArrayList<LocalGroup> result = new ArrayList<LocalGroup>();
            for (LMAccess.GROUP_INFO_1 lgpi : groups) {
                LocalGroup lgp = new LocalGroup();
                lgp.name = lgpi.grpi1_name.toString();
                lgp.comment = lgpi.grpi1_comment.toString();
                result.add(lgp);
            }
            Group[] groupArray = result.toArray(new LocalGroup[0]);
            return groupArray;
        }
        finally {
            int rc3;
            if (bufptr.getValue() != Pointer.NULL && 0 != (rc3 = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                throw new Win32Exception(rc3);
            }
        }
    }

    public static User[] getUsers() {
        return Netapi32Util.getUsers(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static User[] getUsers(String serverName) {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesRead = new IntByReference();
        IntByReference totalEntries = new IntByReference();
        try {
            int rc2 = Netapi32.INSTANCE.NetUserEnum(serverName, 1, 0, bufptr, -1, entriesRead, totalEntries, null);
            if (0 != rc2 || bufptr.getValue() == Pointer.NULL) {
                throw new Win32Exception(rc2);
            }
            LMAccess.USER_INFO_1 user = new LMAccess.USER_INFO_1(bufptr.getValue());
            LMAccess.USER_INFO_1[] users = (LMAccess.USER_INFO_1[])user.toArray(entriesRead.getValue());
            ArrayList<User> result = new ArrayList<User>();
            for (LMAccess.USER_INFO_1 lu2 : users) {
                User auser = new User();
                auser.name = lu2.usri1_name.toString();
                result.add(auser);
            }
            User[] userArray = result.toArray(new User[0]);
            return userArray;
        }
        finally {
            int rc3;
            if (bufptr.getValue() != Pointer.NULL && 0 != (rc3 = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                throw new Win32Exception(rc3);
            }
        }
    }

    public static Group[] getCurrentUserLocalGroups() {
        return Netapi32Util.getUserLocalGroups(Secur32Util.getUserNameEx(2));
    }

    public static Group[] getUserLocalGroups(String userName) {
        return Netapi32Util.getUserLocalGroups(userName, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Group[] getUserLocalGroups(String userName, String serverName) {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesread = new IntByReference();
        IntByReference totalentries = new IntByReference();
        try {
            int rc2 = Netapi32.INSTANCE.NetUserGetLocalGroups(serverName, userName, 0, 0, bufptr, -1, entriesread, totalentries);
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
            LMAccess.LOCALGROUP_USERS_INFO_0 lgroup = new LMAccess.LOCALGROUP_USERS_INFO_0(bufptr.getValue());
            LMAccess.LOCALGROUP_USERS_INFO_0[] lgroups = (LMAccess.LOCALGROUP_USERS_INFO_0[])lgroup.toArray(entriesread.getValue());
            ArrayList<LocalGroup> result = new ArrayList<LocalGroup>();
            for (LMAccess.LOCALGROUP_USERS_INFO_0 lgpi : lgroups) {
                LocalGroup lgp = new LocalGroup();
                lgp.name = lgpi.lgrui0_name.toString();
                result.add(lgp);
            }
            Group[] groupArray = result.toArray(new Group[0]);
            return groupArray;
        }
        finally {
            int rc3;
            if (bufptr.getValue() != Pointer.NULL && 0 != (rc3 = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                throw new Win32Exception(rc3);
            }
        }
    }

    public static Group[] getUserGroups(String userName) {
        return Netapi32Util.getUserGroups(userName, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Group[] getUserGroups(String userName, String serverName) {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesread = new IntByReference();
        IntByReference totalentries = new IntByReference();
        try {
            int rc2 = Netapi32.INSTANCE.NetUserGetGroups(serverName, userName, 0, bufptr, -1, entriesread, totalentries);
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
            LMAccess.GROUP_USERS_INFO_0 lgroup = new LMAccess.GROUP_USERS_INFO_0(bufptr.getValue());
            LMAccess.GROUP_USERS_INFO_0[] lgroups = (LMAccess.GROUP_USERS_INFO_0[])lgroup.toArray(entriesread.getValue());
            ArrayList<Group> result = new ArrayList<Group>();
            for (LMAccess.GROUP_USERS_INFO_0 lgpi : lgroups) {
                Group lgp = new Group();
                lgp.name = lgpi.grui0_name.toString();
                result.add(lgp);
            }
            Group[] groupArray = result.toArray(new Group[0]);
            return groupArray;
        }
        finally {
            int rc3;
            if (bufptr.getValue() != Pointer.NULL && 0 != (rc3 = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                throw new Win32Exception(rc3);
            }
        }
    }

    public static DomainController getDC() {
        DsGetDC.PDOMAIN_CONTROLLER_INFO.ByReference pdci = new DsGetDC.PDOMAIN_CONTROLLER_INFO.ByReference();
        int rc2 = Netapi32.INSTANCE.DsGetDcName(null, null, null, null, 0, pdci);
        if (0 != rc2) {
            throw new Win32Exception(rc2);
        }
        DomainController dc2 = new DomainController();
        dc2.address = pdci.dci.DomainControllerAddress.toString();
        dc2.addressType = pdci.dci.DomainControllerAddressType;
        dc2.clientSiteName = pdci.dci.ClientSiteName.toString();
        dc2.dnsForestName = pdci.dci.DnsForestName.toString();
        dc2.domainGuid = pdci.dci.DomainGuid;
        dc2.domainName = pdci.dci.DomainName.toString();
        dc2.flags = pdci.dci.Flags;
        dc2.name = pdci.dci.DomainControllerName.toString();
        rc2 = Netapi32.INSTANCE.NetApiBufferFree(pdci.dci.getPointer());
        if (0 != rc2) {
            throw new Win32Exception(rc2);
        }
        return dc2;
    }

    public static DomainTrust[] getDomainTrusts() {
        return Netapi32Util.getDomainTrusts(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DomainTrust[] getDomainTrusts(String serverName) {
        DsGetDC.PDS_DOMAIN_TRUSTS.ByReference domains = new DsGetDC.PDS_DOMAIN_TRUSTS.ByReference();
        NativeLongByReference domainCount = new NativeLongByReference();
        int rc2 = Netapi32.INSTANCE.DsEnumerateDomainTrusts(serverName, new NativeLong(63L), domains, domainCount);
        if (0 != rc2) {
            throw new Win32Exception(rc2);
        }
        try {
            int domainCountValue = domainCount.getValue().intValue();
            ArrayList<DomainTrust> trusts = new ArrayList<DomainTrust>(domainCountValue);
            for (DsGetDC.DS_DOMAIN_TRUSTS trust : domains.getTrusts(domainCountValue)) {
                DomainTrust t2 = new DomainTrust();
                t2.DnsDomainName = trust.DnsDomainName.toString();
                t2.NetbiosDomainName = trust.NetbiosDomainName.toString();
                t2.DomainSid = trust.DomainSid;
                t2.DomainSidString = Advapi32Util.convertSidToStringSid(trust.DomainSid);
                t2.DomainGuid = trust.DomainGuid;
                t2.DomainGuidString = Ole32Util.getStringFromGUID(trust.DomainGuid);
                t2.flags = trust.Flags.intValue();
                trusts.add(t2);
            }
            DomainTrust[] domainTrustArray = trusts.toArray(new DomainTrust[0]);
            return domainTrustArray;
        }
        finally {
            rc2 = Netapi32.INSTANCE.NetApiBufferFree(domains.getPointer());
            if (0 != rc2) {
                throw new Win32Exception(rc2);
            }
        }
    }

    public static UserInfo getUserInfo(String accountName) {
        return Netapi32Util.getUserInfo(accountName, Netapi32Util.getDCName());
    }

    public static UserInfo getUserInfo(String accountName, String domainName) {
        PointerByReference bufptr = new PointerByReference();
        int rc2 = -1;
        try {
            rc2 = Netapi32.INSTANCE.NetUserGetInfo(Netapi32Util.getDCName(), accountName, 23, bufptr);
            if (rc2 == 0) {
                LMAccess.USER_INFO_23 info_23 = new LMAccess.USER_INFO_23(bufptr.getValue());
                UserInfo userInfo = new UserInfo();
                userInfo.comment = info_23.usri23_comment.toString();
                userInfo.flags = info_23.usri23_flags;
                userInfo.fullName = info_23.usri23_full_name.toString();
                userInfo.name = info_23.usri23_name.toString();
                userInfo.sidString = Advapi32Util.convertSidToStringSid(info_23.usri23_user_sid);
                userInfo.sid = info_23.usri23_user_sid;
                UserInfo userInfo2 = userInfo;
                return userInfo2;
            }
            throw new Win32Exception(rc2);
        }
        finally {
            if (bufptr.getValue() != Pointer.NULL) {
                Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
            }
        }
    }

    public static class DomainTrust {
        public String NetbiosDomainName;
        public String DnsDomainName;
        public WinNT.PSID DomainSid;
        public String DomainSidString;
        public Guid.GUID DomainGuid;
        public String DomainGuidString;
        private int flags;

        public boolean isInForest() {
            return (this.flags & 1) != 0;
        }

        public boolean isOutbound() {
            return (this.flags & 2) != 0;
        }

        public boolean isRoot() {
            return (this.flags & 4) != 0;
        }

        public boolean isPrimary() {
            return (this.flags & 8) != 0;
        }

        public boolean isNativeMode() {
            return (this.flags & 0x10) != 0;
        }

        public boolean isInbound() {
            return (this.flags & 0x20) != 0;
        }
    }

    public static class DomainController {
        public String name;
        public String address;
        public int addressType;
        public Guid.GUID domainGuid;
        public String domainName;
        public String dnsForestName;
        public int flags;
        public String clientSiteName;
    }

    public static class LocalGroup
    extends Group {
        public String comment;
    }

    public static class UserInfo
    extends User {
        public String fullName;
        public String sidString;
        public WinNT.PSID sid;
        public int flags;
    }

    public static class User {
        public String name;
        public String comment;
    }

    public static class Group {
        public String name;
    }
}

