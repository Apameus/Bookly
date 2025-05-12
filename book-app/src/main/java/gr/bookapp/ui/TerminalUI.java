package gr.bookapp.ui;

import gr.bookapp.models.User;

public final class TerminalUI {
    private final LoginPanelUI loginPanelUI;
    private final AdminPanelUI adminPanelUI;
    private final EmployeePanel employeePanel;

    public TerminalUI(LoginPanelUI loginPanelUI, AdminPanelUI adminPanelUI, EmployeePanel employeePanel) {
        this.loginPanelUI = loginPanelUI;
        this.adminPanelUI = adminPanelUI;
        this.employeePanel = employeePanel;
    }

    public void start(){
        loginPanelUI.checkAndCreateAdmin();

        while (true){
            User user = loginPanelUI.login();
            if (user == null) continue;
            boolean login = true;

            while (login) {
                login = user.isAdmin()
                        ? adminPanelUI.handleAdminActions()
                        : employeePanel.handleEmployeeActions();
            }
        }
    }

}
