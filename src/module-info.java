module name {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;     // <-- THIS is what you are missing
    requires java.desktop; // sometimes needed for UI-related stuff

    opens applicationMain to javafx.fxml;
    opens guiUserLogin to javafx.fxml;
    opens guiNewAccount to javafx.fxml;
    opens guiUserUpdate to javafx.fxml;
    opens guiViewPosts to javafx.fxml;
    opens guiRole1 to javafx.fxml;
    opens guiRole2 to javafx.fxml;
    opens guiAdminHome to javafx.fxml;
    opens guiFirstAdmin to javafx.fxml;
    opens guiAddRemoveRoles to javafx.fxml;
    opens guiMultipleRoleDispatch to javafx.fxml;
    opens guiTools to javafx.fxml;

    exports applicationMain;
}