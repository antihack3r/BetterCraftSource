/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.group;

import com.google.gson.Gson;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import net.labymod.main.Source;
import net.labymod.support.util.Debug;
import net.labymod.user.User;
import net.labymod.user.group.GroupData;
import net.labymod.user.group.LabyGroup;

public class GroupManager {
    public static final LabyGroup DEFAULT_GROUP = new LabyGroup(0, "user", "User", null, 'f', "USER", "NONE", null, null).init();
    public static final short GROUP_ID_PLUS = 10;
    private final Gson gson = new Gson();
    private final ExecutorService executorService;
    private Map<Short, LabyGroup> groups = new HashMap<Short, LabyGroup>();
    private Short[] groupOrder = new Short[0];

    public GroupManager(ExecutorService executorService) {
        this.executorService = executorService;
        this.load();
    }

    public void load() {
        this.executorService.execute(new Runnable(){

            @Override
            public void run() {
                try {
                    HashMap<Short, LabyGroup> map = new HashMap<Short, LabyGroup>();
                    HttpURLConnection connection = (HttpURLConnection)new URL("http://dl.labymod.net/groups.json").openConnection();
                    connection.setRequestProperty("User-Agent", Source.getUserAgent());
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(2000);
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode / 100 == 2) {
                        String jsonString = "";
                        Scanner scanner = new Scanner(connection.getInputStream());
                        while (scanner.hasNextLine()) {
                            jsonString = String.valueOf(jsonString) + scanner.nextLine();
                        }
                        scanner.close();
                        GroupData groupData = GroupManager.this.gson.fromJson(jsonString, GroupData.class);
                        LabyGroup[] labyGroups = groupData.getGroups();
                        Short[] array = new Short[labyGroups.length];
                        int index = 0;
                        LabyGroup[] labyGroupArray = labyGroups;
                        int n2 = labyGroups.length;
                        int n3 = 0;
                        while (n3 < n2) {
                            LabyGroup group = labyGroupArray[n3];
                            short id2 = (short)group.getId();
                            group.init();
                            map.put(id2, group);
                            array[index] = id2;
                            ++index;
                            ++n3;
                        }
                        GroupManager.this.groups = map;
                        GroupManager.this.groupOrder = array;
                    } else {
                        Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Wrong response code while loading groups: " + responseCode);
                    }
                }
                catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });
    }

    public LabyGroup getGroupById(short id2) {
        if (id2 == DEFAULT_GROUP.getId()) {
            return DEFAULT_GROUP;
        }
        return this.groups.get(id2);
    }

    public boolean hasPermissionOf(User user, short groupId) {
        LabyGroup userGroup = user.getGroup();
        if (groupId == DEFAULT_GROUP.getId()) {
            return true;
        }
        if (userGroup == null) {
            return false;
        }
        short userGroupId = (short)userGroup.getId();
        Short[] shortArray = this.groupOrder;
        int n2 = this.groupOrder.length;
        int n3 = 0;
        while (n3 < n2) {
            short orderId = shortArray[n3];
            if (orderId == userGroupId) {
                return true;
            }
            if (orderId == groupId) {
                return false;
            }
            ++n3;
        }
        return false;
    }
}

