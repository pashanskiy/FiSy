package FiSy;



import FiSy.StoreData.StoreLogin;
import FiSy.StoreData.StoreRegister;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class DataBase {
    public static Connection conn;
    public static String mainDirectory;
     public DataBase(){
         //String url = "jdbc:sqlite:C:/sqlite/db/";
         //String url = "dbc:sqlite::resource:mydb.db";
         mainDirectory = "."+File.separator+"FiSy Server Recources";
         String url = "jdbc:sqlite:."+File.separator+"FiSy Server Recources"+File.separator+"11111111-1111-1111-1111-111111111111";
         try {
             File file = new File("."+File.separator+"FiSy Server Recources"+File.separator+"11111111-1111-1111-1111-111111111111");
             if(!file.exists()) FileEngine.ExportResource("resources/11111111-1111-1111-1111-111111111111");

             conn = DriverManager.getConnection(url);
         }catch (Exception e){e.printStackTrace();}
     }

    public static boolean userRegister(StoreRegister data){

        if(data.getLogin().length()<=32 && data.getLogin().length()>=4&&
                data.getPasswordhash().length()>0 &&
                data.getPasswordsalt().length>0 &&
                data.getName().length()<=32 && data.getName().length()>=2&&
                data.getSurname().length()<=32 && data.getSurname().length()>=2&&
                data.getMiddlename().length()>= 0&& data.getGroup().length()>=0&&
                data.getRsaPubK()!=null&&
                data.getRsaPrivK()!=null&&
                data.getDatabaseKey()!=null){
            try {
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT login FROM users WHERE login=?;");
                preparedStatement.setString(1,data.getLogin());
                ResultSet rs = preparedStatement.executeQuery();
                if(!rs.next()) {

                    preparedStatement.clearParameters();
                    preparedStatement = conn.prepareStatement("INSERT OR IGNORE INTO name_table (uname) VALUES (?);");
                    preparedStatement.setString(1,data.getName());
                    preparedStatement.execute();

                    preparedStatement.clearParameters();
                    preparedStatement = conn.prepareStatement("INSERT OR IGNORE INTO ssurname  (ssurname) VALUES (?);");
                    preparedStatement.setString(1,data.getSurname());
                    preparedStatement.execute();

                    preparedStatement.clearParameters();
                    preparedStatement = conn.prepareStatement("INSERT OR IGNORE INTO middlename_table (middlename) VALUES (?);");
                    preparedStatement.setString(1,data.getMiddlename());
                    preparedStatement.execute();

                    preparedStatement.clearParameters();
                    preparedStatement = conn.prepareStatement("INSERT OR IGNORE INTO group_table (ugroup) VALUES (?);");
                    preparedStatement.setString(1,data.getGroup());
                    preparedStatement.execute();

                    preparedStatement.clearParameters();
                    preparedStatement = conn.prepareStatement("INSERT INTO users (id_name, id_surname, id_middle_name, id_user_group, login, password_hash, password_salt, user_directory, id_user_power, RSA_public_key, RSA_private_key, DataBase_key) " +
                            "VALUES ((select id from name_table where uname = '"+data.getName()+"')," +
                            "(select id from ssurname  where ssurname = '"+data.getSurname()+"')," +
                            "(select id from middlename_table where middlename = '"+data.getMiddlename()+"')," +
                            "(select id from group_table where ugroup = '"+data.getMiddlename()+"'),?,?,?,?,?,?,?,?)");
                    preparedStatement.setString(1,data.getLogin());
                    preparedStatement.setString(2,data.getPasswordhash());
                    preparedStatement.setBytes(3,data.getPasswordsalt());
                    preparedStatement.setString(4,"Nothing");
                    preparedStatement.setInt(5,3);
                    preparedStatement.setString(6, data.getRsaPubK());
                    preparedStatement.setString(7, data.getRsaPrivK());
                    preparedStatement.setBytes(8, data.getDatabaseKey());
                    preparedStatement.executeUpdate();
                    System.out.println("The success user registration!-> "+data.getLogin());
                    return true;
                }else {
                    return false;}

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static boolean userLoginGetPasswordSalt(StoreLogin data){

        if(data.getLogin().length()<=32 && data.getLogin().length()>=4){
            try {
                //PreparedStatement preparedStatement = conn.prepareStatement("SELECT id, login, password_hash, password_salt, user_directory, DataBase_key, user_power, RSA_private_key, RSA_public_key FROM users WHERE login=?;");
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT a.id, a.login, a.password_hash, a.password_salt, a.user_directory, a.DataBase_key, b.userpower, a.RSA_private_key, a.RSA_public_key FROM users a INNER JOIN userpowertable b ON a.id_user_power=b.id WHERE login=?;");
                preparedStatement.setString(1,data.getLogin());
                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next()) {
                    data.setUserId(rs.getInt("id"));
                    data.setLogin(rs.getString("login"));
                    data.setPasswordhash(rs.getString("password_hash"));
                    data.setPasswordsalt(rs.getBytes("password_salt"));
                    data.setDirectory(rs.getString("user_directory"));
                    data.setDatabaseKey(rs.getBytes("DataBase_key"));
                    data.setUserPower(rs.getString("userpower"));
                    data.setRsaPrivK(rs.getString("RSA_private_key"));
                    data.setRsaPubK(rs.getString("RSA_public_key"));
                    if (data.getDirectory().equals("Nothing")){
                        String uuid = String.valueOf(UUID.randomUUID());
                        preparedStatement = null;
                        preparedStatement = conn.prepareStatement("UPDATE users SET user_directory=? WHERE login=?");
                        preparedStatement.setString(1, uuid);
                        preparedStatement.setString(2, data.getLogin());
                        preparedStatement.executeUpdate();
                        data.setDirectory(uuid);
                    }
                    return true;
                }else {
                    return false;}

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static StoreLogin getUsers(ArrayList<Integer>usi){
            try {
                StoreLogin storeLogin = new StoreLogin();
                for(Integer us:usi) {
                    PreparedStatement preparedStatement = conn.prepareStatement("SELECT a.login,b.uname,t.ssurname,e.middlename,f.ugroup,a.user_directory FROM users a " +
                                                                                     "INNER JOIN name_table b ON a.id_name=b.id " +
                                                                                        "INNER JOIN ssurname t ON a.id_surname = t.id " +
                                                                                          "INNER JOIN middlename_table e ON a.id_middle_name=e.id " +
                                                                                            "INNER JOIN group_table f ON a.id_user_group=f.id WHERE a.id=?;");
                    preparedStatement.setInt(1, us);
                    ResultSet rs = preparedStatement.executeQuery();
                    if(rs.next()){
                        StoreLogin.SUser sUser=new StoreLogin.SUser();
                        sUser.setUserid(us);
                        sUser.setLogin(rs.getString("login"));
                        sUser.setName(rs.getString("uname"));
                        sUser.setSurname(rs.getString("ssurname"));
                        sUser.setMiddlename(rs.getString("middlename"));
                        sUser.setGroup(rs.getString("ugroup"));
                        sUser.setDirectory(rs.getString("user_directory"));
                        storeLogin.getSUser().add(sUser);
                    }
                }
                return storeLogin;
            }catch (Exception e){e.printStackTrace();}
         return null;
    }

    public static boolean getHighUsers(StoreLogin data){
            try {

                PreparedStatement preparedStatement = conn.prepareStatement("SELECT id_user FROM subordinate_table WHERE id_subordinate_user=?;");
                preparedStatement.setInt(1,data.getUserId());
                ResultSet rs = preparedStatement.executeQuery();
                ArrayList<Integer> highUsers=new ArrayList<>();
                while (rs.next()) {
                    highUsers.add(rs.getInt("id_user"));
                }
                for(Integer id:highUsers){
                    preparedStatement = conn.prepareStatement("SELECT RSA_public_key FROM users WHERE id=?;");
                    preparedStatement.setInt(1,id);
                    rs = preparedStatement.executeQuery();
                    if(rs.next()) {
                        StoreLogin.SUser sUser = new StoreLogin.SUser();
                        sUser.setUserid(id);
                        sUser.setPublickey((rs.getString("RSA_public_key")));
                        data.getSUser().add(sUser);
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<Integer> getLowUsers(StoreLogin data){
            try {
                PreparedStatement preparedStatement = conn.prepareStatement("select id_subordinate_user from subordinate_table where id_user=?");
                preparedStatement.setInt(1,data.getUserId());
                ResultSet rs = preparedStatement.executeQuery();
                ArrayList<Integer> lu=new ArrayList<>();
                while (rs.next()) {
                    lu.add(rs.getInt("id_subordinate_user"));
                }
                return lu;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    public static StoreLogin.SUser getLowUserPass(Integer[] users){
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select subordinate_user_database_key from subordinate_table where id_subordinate_user=? AND id_user=?");
            preparedStatement.setInt(1, users[0]);
            preparedStatement.setInt(2, users[1]);
            ResultSet rs = preparedStatement.executeQuery();
            StoreLogin.SUser sUser = new StoreLogin.SUser();
            if(rs.next()){
                sUser.setUserid(users[0]);
                sUser.setUserDBkey(rs.getBytes("subordinate_user_database_key"));

                preparedStatement = conn.prepareStatement("select user_directory from users where id=?");
                preparedStatement.setInt(1, users[0]);
                rs = preparedStatement.executeQuery();
                if(rs.next()){
                    sUser.setDirectory(rs.getString("user_directory"));
                }
            }
            return sUser;
        }catch (Exception e){e.printStackTrace();}
        return null;
     }

    public static ArrayList<Integer> getLowUsersWithPass(StoreLogin data){
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select id_subordinate_user from subordinate_table where id_user=? AND subordinate_user_database_key IS NOT NULL ");
            preparedStatement.setInt(1,data.getUserId());
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<Integer> lu=new ArrayList<>();
            while (rs.next()) {
                lu.add(rs.getInt("id_subordinate_user"));
            }
            return lu;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setKeyToHighUsers(StoreLogin data) {
         try {
             for (StoreLogin.SUser sUser : data.getSUser()) {
                 PreparedStatement preparedStatement = conn.prepareStatement("UPDATE subordinate_table SET subordinate_user_database_key=? WHERE id_user=? AND id_subordinate_user=?");
                 preparedStatement.setBytes(1, sUser.getUserDBKey());
                 preparedStatement.setInt(2, sUser.getUserID());
                 preparedStatement.setInt(3, data.getUserId());
                 preparedStatement.executeUpdate();
             }
             return  true;
         }catch (Exception e){e.printStackTrace();}
         return false;
    }

    public static boolean getAllUsers(StoreLogin data){
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT a.id, b.uname, t.ssurname ,t2.middlename, g.ugroup ,a.login, u.userpower  FROM users a " +
                    "INNER JOIN name_table b ON a.id_name=b.id " +
                    "INNER JOIN ssurname t ON a.id_surname = t.id " +
                    "INNER JOIN middlename_table t2 ON a.id_middle_name = t2.id " +
                    "INNER JOIN group_table g ON a.id_user_group = g.id " +
                    "INNER JOIN userpowertable u ON a.id_user_power = u.id");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                StoreLogin.SUser sUser = new StoreLogin.SUser();
                sUser.setUserid(rs.getInt("id"));
                sUser.setName(rs.getString("uname"));
                sUser.setSurname(rs.getString("ssurname"));
                sUser.setMiddlename(rs.getString("middlename"));
                sUser.setGroup(rs.getString("ugroup"));
                sUser.setLogin(rs.getString("login"));
                sUser.setType(rs.getString("userpower"));
                data.getSUser().add(sUser);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setNewUsersRules(ArrayList<Integer[]> data) {
        try {
            for (Integer[] integers : data) {
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT id,id_user,id_subordinate_user FROM subordinate_table WHERE id_user=? AND id_subordinate_user=?");
                preparedStatement.setInt(1, integers[0]);
                preparedStatement.setInt(2, integers[1]);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    if (integers[2] == 0) {
                        preparedStatement = conn.prepareStatement("DELETE FROM subordinate_table WHERE id_user=? AND id_subordinate_user=?;");
                        preparedStatement.setInt(1, integers[0]);
                        preparedStatement.setInt(2, integers[1]);
                        preparedStatement.executeUpdate();
                    }
                } else {
                    if (integers[2] == 1) {
                        preparedStatement = conn.prepareStatement("INSERT INTO subordinate_table (id_user, id_subordinate_user) VALUES(?,?);");
                        preparedStatement.setInt(1, integers[0]);
                        preparedStatement.setInt(2, integers[1]);
                        preparedStatement.executeUpdate();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setNewUsersRights(ArrayList<String[]> data){
        try {
            for (String[] strings : data) {
                PreparedStatement preparedStatement = conn.prepareStatement("UPDATE users SET id_user_power=(select id from userpowertable where userpower = ?) WHERE id=?");
                preparedStatement.setString(1, strings[1]);
                preparedStatement.setInt(2, Integer.parseInt(strings[0]));
                preparedStatement.executeUpdate();

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void ownerClear(StoreLogin storeLogin){
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT id FROM users WHERE login=?");
            preparedStatement.setString(1, "OWNER");
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                int uid = rs.getInt("id");
                preparedStatement = conn.prepareStatement("DELETE FROM subordinate_table WHERE id_user=?;");
                preparedStatement.setInt(1, uid);

                preparedStatement = conn.prepareStatement("UPDATE users SET password_hash=?, password_salt=?, DataBase_key=?, RSA_public_key=?, RSA_private_key=?, id_user_power=? WHERE id=?");
                preparedStatement.setString(1, storeLogin.getPasswordhash());
                preparedStatement.setBytes(2,storeLogin.getPasswordsalt());
                preparedStatement.setBytes(3,storeLogin.getDatabaseKey());
                preparedStatement.setString(4,storeLogin.getRsaPubK());
                preparedStatement.setString(5,storeLogin.getRsaPrivK());
                preparedStatement.setInt(6,1);
                preparedStatement.setInt(7,uid);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
