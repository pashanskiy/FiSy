package FiSy;

import javafx.beans.property.SimpleStringProperty;

public class tablePerson {
    public static class LoginPerson {
        private final SimpleStringProperty info;
        private final SimpleStringProperty address;
        private final SimpleStringProperty port;

        public LoginPerson(String i, String a, String p) {
            this.info = new SimpleStringProperty(i);
            this.address = new SimpleStringProperty(a);
            this.port = new SimpleStringProperty(p);
        }

        public String getInfo() {
            return info.get();
        }
        public void setInfo(String info1) {
            info.set(info1);
        }

        public String getAddress() {
            return address.get();
        }
        public void setAddress(String address1) {
            address.set(address1);
        }

        public String getPort() {
            return port.get();
        }
        public void setPort(String port1) {
            port.set(port1);
        }
    }

    public static class FileDialogPerson {
        private final SimpleStringProperty name;
        private final SimpleStringProperty size;
        private final SimpleStringProperty type;
        private final SimpleStringProperty date;

        public FileDialogPerson(String Name, String Size, String Type, String AddDate) {
            this.name = new SimpleStringProperty(Name);
            this.size = new SimpleStringProperty(Size);
            this.type = new SimpleStringProperty(Type);
            this.date = new SimpleStringProperty(AddDate);
        }

        public String getName() {
            return name.get();
        }
        public void setName(String Name) {
            name.set(Name);
        }

        public String getSize() {
            return size.get();
        }
        public void setSize(String Size) {
            size.set(Size);
        }

        public String getType() {
            return type.get();
        }
        public void setType(String Type) {
            type.set(Type);
        }

        public String getDatedate() {
            return date.get();
        }
        public void setDatedateate(String AddDate) {
            date.set(AddDate);
        }
    }

    public static class AdminDialogPerson {
        private SimpleStringProperty userid;
        private SimpleStringProperty name;
        private SimpleStringProperty surname;
        private SimpleStringProperty middlename;
        private SimpleStringProperty group;
        private SimpleStringProperty login;
        private SimpleStringProperty userdirectory;
        private SimpleStringProperty type;

        public AdminDialogPerson(String UserID, String Name, String Surname, String MiddleName, String Group, String Login, String Directory, String Type) {
            this.userid = new SimpleStringProperty(UserID);
            this.name = new SimpleStringProperty(Name);
            this.surname = new SimpleStringProperty(Surname);
            this.middlename = new SimpleStringProperty(MiddleName);
            this.group = new SimpleStringProperty(Group);
            this.login = new SimpleStringProperty(Login);
            this.userdirectory = new SimpleStringProperty(Directory);
            this.type = new SimpleStringProperty(Type);
        }

        public String getUsrid() {
            return userid.get();
        }
        public void setUsrid(String userID) {
            userid.set(userID);
        }

        public String getName() {
            return name.get();
        }
        public void setName(String Name) {
            name.set(Name);
        }

        public String getSurname() {
            return surname.get();
        }
        public void setSurname(String Surname) {
            surname.set(Surname);
        }

        public String getMiddlename() {
            return middlename.get();
        }
        public void setMiddlename(String Name) {
            middlename.set(Name);
        }

        public String getGroup() {
            return group.get();
        }
        public void setGroup(String Group) {
            group.set(Group);
        }

        public String getLogin() {
            return login.get();
        }
        public void setLogin(String Login) {
            login.set(Login);
        }

        public String getDir() {
            return userdirectory.get();
        }
        public void setDir(String Dir) {
            userdirectory.set(Dir);
        }

        public String getType() {
            return type.get();
        }
        public void setType(String Type) {
            type.set(Type);
        }
    }

    public static class AdminSetRightDialogPerson {
        private SimpleStringProperty userid;
        private SimpleStringProperty name;
        private SimpleStringProperty surname;
        private SimpleStringProperty middlename;
        private SimpleStringProperty group;
        private SimpleStringProperty login;
        private SimpleStringProperty type;

        public AdminSetRightDialogPerson(String UserID, String Name, String Surname, String Middlename, String Group, String Login, String Type) {
            this.userid = new SimpleStringProperty(UserID);
            this.name = new SimpleStringProperty(Name);
            this.surname = new SimpleStringProperty(Surname);
            this.middlename = new SimpleStringProperty(Middlename);
            this.group = new SimpleStringProperty(Group);
            this.login = new SimpleStringProperty(Login);
            this.type = new SimpleStringProperty(Type);
        }

        public String getUsrid() {
            return userid.get();
        }
        public void setUsrid(String userID) {
            userid.set(userID);
        }

        public String getName() {
            return name.get();
        }
        public void setName(String Name) {
            name.set(Name);
        }

        public String getSurname() {
            return surname.get();
        }
        public void setSurname(String Surname) {
            surname.set(Surname);
        }

        public String getMiddlename() {
            return middlename.get();
        }
        public void setMiddlename(String Name) {
            middlename.set(Name);
        }

        public String getGroup() {
            return group.get();
        }
        public void setGroup(String Group) {
            group.set(Group);
        }

        public String getLogin() {
            return login.get();
        }
        public void setLogin(String Login) {
            login.set(Login);
        }


        public String getType() {
            return type.get();
        }
        public void setType(String Type) {
            type.set(Type);
        }
    }

    public static class UserDialogPerson {
        private SimpleStringProperty name;
        private SimpleStringProperty surname;
        private SimpleStringProperty middlename;
        private SimpleStringProperty group;
        private SimpleStringProperty login;

        public UserDialogPerson (String Name, String Surname, String Middlename, String Group, String Login) {
            this.name = new SimpleStringProperty(Name);
            this.surname = new SimpleStringProperty(Surname);
            this.middlename = new SimpleStringProperty(Middlename);
            this.group = new SimpleStringProperty(Group);
            this.login = new SimpleStringProperty(Login);
        }
        public String getName() {
            return name.get();
        }
        public void setName(String Name) {
            name.set(Name);
        }

        public String getSurname() {
            return surname.get();
        }
        public void setSurname(String Surname) {
            surname.set(Surname);
        }

        public String getMiddlename() {
            return middlename.get();
        }
        public void setMiddlename(String Name) {
            middlename.set(Name);
        }

        public String getGroup() {
            return group.get();
        }
        public void setGroup(String Group) {
            group.set(Group);
        }

        public String getLogin() {
            return login.get();
        }
        public void setLogin(String Login) {
            login.set(Login);
        }

    }

}
