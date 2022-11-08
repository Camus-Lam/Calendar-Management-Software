package system.menu;

import Calender.*;
import gui.MainMenuGUI;
import gui.SetTimeGUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The main menu is the facade class responsible for calling other methods in UseCases.ViewManagement and UseCases.CalenderManagement
 */
public class MainMenu extends TimerTask {
    User currentUser;
    TimeThread timeThread;
    volatile LocalDateTime systemTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    ViewFactory viewFactory;
    ViewFactoryBuilder viewBuilder;
    boolean go = true;
    MainMenuGUI mainMenuGUI;
    Timer updateTimeThread;
    Timer updateMenuThread;

    /**
     * The constructor for the main menu
     *
     * @param currentUser the user that is currently logged in
     */
    public MainMenu(User currentUser) {
        this.currentUser = currentUser;
        this.timeThread = new TimeThread();
        this.viewBuilder = new ViewFactoryBuilder(currentUser.getCurrentCalender());

        //Build the view
        this.viewBuilder.buildCalenderManagement();
        this.viewBuilder.buildViewMangement();
        this.viewBuilder.buildView();
        this.viewFactory = this.viewBuilder.getView();
        this.mainMenuGUI = new MainMenuGUI(this);
        this.mainMenuGUI.setVisible(true);

        //Set the timer threads
        this.updateMenuThread = new Timer();
        this.updateTimeThread = new Timer();
        this.updateTimeThread.schedule(timeThread, 0, 5000);
        this.updateMenuThread.schedule(this, 0, 5000);
    }

    /**
     * Ends the threads that update the time
     */
    public void endThreads(){
        this.updateMenuThread.cancel();
        this.updateTimeThread.cancel();
    }

    /**
     * This method will allow for fast forwarding time
     *
     * @param newDate the new date you wish to fast forward too
     */
    public synchronized void setSystemTime(LocalDateTime newDate) {
        this.systemTime = newDate;
    }

    public synchronized void setSystemTimeUser() {
        SetTimeGUI setTimeGUI = new SetTimeGUI(this.timeThread, formatter);
    }

    public void updateAll() {
        this.viewFactory.updateAll(this.systemTime);
    }

    public boolean keepGoing() {
        return this.go;
    }

    public ViewFactory getView(){
        return this.viewFactory;
    }

    public User getCurrentUser(){
        return this.currentUser;
    }

//    public void mainScreen() {
//        System.out.println("Please select a option from below: \n" +
//                "1) Create Events\n" +
//                "2) Create and Add Alerts\n" +
//                "3) Create Memos\n" +
//                "4) Create and Add Tags\n" +
//                "5) Link Events into Series\n" +
//                "6) View future events\n" +
//                "7) View Events with name\n" +
//                "8) View Events with memos\n" +
//                "9) View Events with tag\n" +
//                "10) View Events in series\n" +
//                "11) View All Alert\n" +
//                "12) View Past Events\n" +
//                "13) Set time\n" +
//                "14) Edit Memo info\n" +
//                "15) Remove Memo\n" +
//                "16) Edit Alert info\n" +
//                "17) Remove Alert\n" +
//                "18) Edit Event info\n" +
//                "19) Remove Event\n" +
//                "-1) Logout");
//
//        try {
//            int userInput = Integer.parseInt(reader.readLine());
//
//            switch (userInput) {
//                case 1:
//                    // Call the view methods here
//                    this.view.userAddEvent();
//                    break;
//                case 2:
//                    this.view.userAddAlert();
//                    break;
//                case 3:
//                    this.view.userAddMemo();
//                    break;
//                case 4:
//                    this.view.userAddTag();
//                    break;
//                case 5:
//                    this.view.userLinkEventsToSeries();
//                    break;
//                case 6:
//                    this.view.viewFutureEvents();
//                    break;
//                case 7:
//                    this.view.displayUserInputEventsByName();
//                    break;
//                case 8:
//                    this.view.displayUserInputEventsWithMemo();
//                    break;
//                case 9:
//                    this.view.displayUserInputEventsWithTag();
//                    break;
//                case 10:
//                    this.view.displayUserInputEventsInSeries();
//                    break;
//                case 11:
//                    this.view.viewAllAlerts();
//                    break;
//                case 12:
//                    this.view.viewPastEvents();
//                    break;
//                case 13:
//                    this.setSystemTimeUser();
//                    break;
//                case 14:
//                    this.view.userEditMemo();
//                    break;
//                case 15:
//                    this.view.removeMemo();
//                    break;
//                case 16:
//                    this.view.userEditAlert();
//                    break;
//                case 17:
//                    this.view.removeAlert();
//                    break;
//                case 18:
//                    this.view.userEditEvent();
//                    break;
//                case 19:
//                    this.view.userRemoveEvent();
//                    break;
//                case -1:
//                    ConnectDB.logout(currentUser);
//                    // Go back to the login screen
//                    this.go = false;
//                    break;
//            }
//
//        } catch (Exception e) {
//            System.out.println("Invalid Input!");
//        }
//    }


    @Override
    public void run() {
        this.systemTime = this.timeThread.getTime();
        this.updateAll();
        this.go = !mainMenuGUI.getLogoutStatus();
    }
}
