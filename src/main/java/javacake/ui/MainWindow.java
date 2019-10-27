package javacake.ui;

import javacake.Duke;
import javacake.commands.CreateNoteCommand;
import javacake.commands.EditNoteCommand;
import javacake.exceptions.DukeException;
import javacake.commands.QuizCommand;
import javacake.notes.NoteList;
import javacake.quiz.Question;
import javacake.storage.Profile;
import javacake.storage.Storage;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for MainWindow. Provides the layout for the other controls.
 */
public class MainWindow extends AnchorPane {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton = new Button();
    @FXML
    private HBox topBar;
    @FXML
    private VBox avatarScreen;
    @FXML
    private ScrollPane taskScreen;
    @FXML
    private VBox taskContainer;
    @FXML
    private ScrollPane noteScreen;
    @FXML
    private VBox noteContainer;
    @FXML
    private Button themeModeButton;
    public static boolean isLightMode = true;
    public static boolean isChanged = false;

    private Duke duke;
    private Stage primaryStage;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/padoru.png"));

    private QuizCommand quizCommand;
    private boolean isQuiz = false;
    private boolean isStarting = true;
    private boolean isTryingReset = false;
    private boolean isWritingNote = false;
    private String input = "";
    private String response = "";

    /**
     * Initialise the Main Window launched.
     */
    @FXML
    public void initialize()  throws DukeException {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        taskScreen.vvalueProperty().bind(taskContainer.heightProperty());
        noteScreen.vvalueProperty().bind(noteContainer.heightProperty());
        avatarScreen.getChildren().add(AvatarScreen.setAvatar(AvatarScreen.AvatarMode.HAPPY));
        topBar.getChildren().add(new TopBar());
        TopBar.setUpProgressBars();

        if (duke.isFirstTimeUser) {
            response = Ui.showWelcomeMsgPhaseA(duke.isFirstTimeUser);
            showContentContainer();
        } else {
            response = Ui.showWelcomeMsgPhaseA(duke.isFirstTimeUser)
                    + Ui.showWelcomeMsgPhaseB(duke.isFirstTimeUser, duke.userName, duke.userProgress);
            showContentContainer();
        }

        showListNotesBox();
        showRemindersBox();
        playGuiModeLoop();
    }

    public void setDuke(Duke d) {
        duke = d;
    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }


    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        //CHECKSTYLE:OFF
        try {
            input = userInput.getText();
            // get input first, don't get response first...
            userInput.clear();
            Duke.logger.log(Level.INFO, "INPUT: " + input);
            DialogBox.isScrollingText = true;
            AvatarScreen.avatarMode = AvatarScreen.AvatarMode.HAPPY;
            if (input.contains("exit")) {
                System.out.println("EXIT");
                Duke.logger.log(Level.INFO, "EXITING PROGRAM!");
                // find out if exit condition
                handleExit();
            } else if (input.contains("listnote")) {
                Duke.logger.log(Level.INFO, "`listnote` command");
                showListNotesBox();
            } else if (input.contains("createnote")) {
                Duke.logger.log(Level.INFO, "`createnote` command");
                response = duke.getResponse(input);
                showContentContainer();
                showListNotesBox();
            } else if (isStarting && duke.isFirstTimeUser) { //set up new username
                System.out.println("start and first");
                Duke.logger.log(Level.INFO, "New user initialising...");
                handleStartAndFirstTime();
            } else if (isTryingReset) { //confirmation of reset
                Duke.logger.log(Level.INFO, "isTryingReset...");
                System.out.println("resetting time");
                handleResetConfirmation();
            } else if (isWritingNote) {
                Duke.logger.log(Level.INFO, "isWritingNote...");
                DialogBox.isScrollingText = false;
                if (input.equals("/save")) {
                    isWritingNote = false;
                    response = EditNoteCommand.successSaveMessage();
                } else {
                    response = EditNoteCommand.writeSaveGui(input);
                }
                showContentContainer();
            } else {
                Duke.logger.log(Level.INFO, "executing normal(else) mode!");
                response = duke.getResponse(input);
                if (isDeadlineRelated()) {
                    //handles "deadline" and "reminder"
                    Duke.logger.log(Level.INFO, "deadline setting");
                } else if (isFirstQuiz()) {
                    Duke.logger.log(Level.INFO, "First Quiz Incoming!");
                } else if (isFirstResetRequest()) {
                    Duke.logger.log(Level.INFO, "Reset command executed!");
                } else if (!isQuiz || isStarting) {
                    //default start: finding of response
                    isStarting = false;
                    Duke.logger.log(Level.INFO, "Response: " + response);
                    //response = duke.getResponse(input);
                    if (response.contains("!@#_EDIT_NOTE")) {
                        Duke.logger.log(Level.INFO, "Editing note initialise!");
                        isWritingNote = true;
                        response = EditNoteCommand.getHeadingMessage();
                        DialogBox.isScrollingText = false;
                        showContentContainer();
                        EditNoteCommand.clearTextFileContent();
                    } else {
                        Duke.logger.log(Level.INFO, "Normal commands mode!");
                        System.out.println("starting BUT not firsttime");
                        showContentContainer();
                    }
                } else if (isQuiz) {
                    //Must be quizCommand: checking of answers
                    Duke.logger.log(Level.INFO, "Quiz Mode!");
                    System.out.println("quiz answer checking");
                    DialogBox.isScrollingText = false;
                    handleGuiQuiz();
                    showContentContainer();
                }
                //System.out.println("End->Next");
            }
        } catch (DukeException e) {
            response = e.getMessage();
            showContentContainer();
            Duke.logger.log(Level.WARNING, e.getMessage());
        }
        //CHECKSTYLE:ON
    }

    @FXML
    private void handleGuiMode() {
        if (isLightMode) { //switches to Dark theme
            isLightMode = false;
            this.setStyle("-fx-background-color: black");
            sendButton.setStyle("-fx-background-color: #333; -fx-border-color: black;");
            themeModeButton.setStyle("-fx-background-color: #333; -fx-border-color: black;");
            topBar.setStyle("-fx-background-color: #BBB; -fx-border-color: grey;");
            userInput.setStyle("-fx-background-color: #9999; -fx-background-radius: 10;");
            dialogContainer.setStyle("-fx-background-color: grey;");
            avatarScreen.setStyle("-fx-background-color: grey;");
            taskContainer.setStyle("-fx-background-color: grey;");
            noteContainer.setStyle("-fx-background-color: grey;");
        } else { //switches to Light theme
            isLightMode = true;
            this.setStyle("-fx-background-color: white");
            sendButton.setStyle("-fx-background-color: #FF9EC7; -fx-border-color: white;");
            themeModeButton.setStyle("-fx-background-color: #FF9EC7; -fx-border-color: white;");
            topBar.setStyle("-fx-background-color: #EE8EC7; -fx-border-color: white;");
            userInput.setStyle("-fx-background-color: #EE8EC7;"
                    + " -fx-background-radius: 10;");
            dialogContainer.setStyle("-fx-background-color: pink;");
            avatarScreen.setStyle("-fx-background-color: #FEE;");
            taskContainer.setStyle("-fx-background-color: pink;");
            noteContainer.setStyle("-fx-background-color: pink;");
        }
    }


    private String getFirstQn(String cmdMode) throws DukeException {
        switch (cmdMode) {
        case "!@#_QUIZ_1":
            quizCommand = new QuizCommand(Question.QuestionType.BASIC, false);
            break;
        case "!@#_QUIZ_2":
            quizCommand = new QuizCommand(Question.QuestionType.OOP, false);
            break;
        case "!@#_QUIZ_3":
            quizCommand = new QuizCommand(Question.QuestionType.EXTENSIONS, false);
            break;
        case "!@#_QUIZ_4":
            quizCommand = new QuizCommand(Question.QuestionType.ALL, false);
            break;
        default:
        }
        return quizCommand.getNextQuestion();
    }

    private void handleExit() {
        response = duke.getResponse(input);
        showContentContainer();
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> primaryStage.hide());
        pause.play();
    }

    private void handleStartAndFirstTime() throws DukeException {
        duke.userName = input;
        duke.profile.overwriteName(duke.userName);
        response = Ui.showWelcomeMsgPhaseB(duke.isFirstTimeUser, duke.userName, duke.userProgress);
        showContentContainer();
        isStarting = false;
    }

    private void handleResetConfirmation() throws DukeException {
        if (input.equals("yes")) {
            //resets
            duke.profile.resetProfile();
            duke.storage.resetStorage();
            duke.profile = new Profile();
            duke.userProgress = duke.profile.getTotalProgress();
            duke.userName = duke.profile.getUsername();
            duke.isFirstTimeUser = true;
            showRemindersBox();
            response = "Reset confirmed!\nPlease type in new username:\n";
            TopBar.resetProgress();
            isStarting = true;
            Duke.logger.log(Level.INFO, "Reset Confirmed!");
        } else {
            response = "Reset cancelled.\nType 'list' to get list of available commands.";
            Duke.logger.log(Level.INFO, "Reset Rejected!");
        }
        showContentContainer();
        isTryingReset = false;
    }



    private void handleGuiQuiz() throws DukeException {
        quizCommand.checkAnswer(input);
        if (quizCommand.questionCounter >= 0) {
            response = quizCommand.getNextQuestion();
        } else {
            isQuiz = false;
            DialogBox.isScrollingText = true;
            response = quizCommand.getQuizScore();
            if (quizCommand.scoreGrade == QuizCommand.ScoreGrade.BAD) {
                AvatarScreen.avatarMode = AvatarScreen.AvatarMode.POUT;
            } else if (quizCommand.scoreGrade == QuizCommand.ScoreGrade.OKAY) {
                AvatarScreen.avatarMode = AvatarScreen.AvatarMode.SAD;
            }
        }
    }

    private void showContentContainer() {
        dialogContainer.getChildren().clear();
        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog(response, dukeImage));
    }

    private void showTaskContainer() {
        taskContainer.getChildren().clear();
        taskContainer.getChildren().add(
                DialogBox.getTaskDialog(response));
    }

    private void showNoteContainer() {
        noteContainer.getChildren().clear();
        noteContainer.getChildren().add(
                DialogBox.getTaskDialog(response));
    }

    private void playGuiModeLoop() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), ev -> {
            if (isLightMode && isChanged) { //change to dark mode
                handleGuiMode();
                isChanged = false;
            }
            if (!isLightMode && isChanged) { //change to light mode
                handleGuiMode();
                isChanged = false;
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private boolean isDeadlineRelated() {
        if (input.length() >= 8 && input.substring(0, 8).equals("deadline")) {
            //response = duke.getResponse(input);
            System.out.println(response);
            if (!response.contains("[!]")) {
                response = duke.getResponse("reminder");
                System.out.println(response);
                //CHECKSTYLE:OFF
                response = response.replaceAll("✓", "\u2713");
                response = response.replaceAll("✗", "\u2717");
                //CHECKSTYLE:ON
                showTaskContainer();
                Duke.logger.log(Level.INFO, "Adding deadlines setting");
            } else {
                response += "\nType 'reminder' to view deadlines";
                showTaskContainer();
                Duke.logger.log(Level.WARNING, "Deadline is not properly parsed!");
            }
            return true;
        } else if (input.equals("reminder")) {
            response = "Reminders are shown over there! ================>>>\n";
            showContentContainer();
            showRemindersBox();
            Duke.logger.log(Level.INFO, "Reminder setting");
            return true;
        }
        return false;
    }

    private boolean isFirstQuiz() throws DukeException {
        if (response.contains("!@#_QUIZ")) {
            //checks for first execution of quizCommand
            isQuiz = true;
            Duke.logger.log(Level.INFO, "isFirstQuiz(): " + response);
            response = getFirstQn(response);
            DialogBox.isScrollingText = false;
            showContentContainer();
            return true;
        }
        return false;
    }

    private boolean isFirstResetRequest() {
        if (response.contains("Confirm reset")) {
            //checks if resetCommand was executed
            Duke.logger.log(Level.INFO, "isFirstResetRequest(): Awaiting confirmation of reset");
            isTryingReset = true;
            showContentContainer();
            return true;
        }
        return false;
    }

    private void showListNotesBox() throws DukeException {
        response = Ui.showNoteList(duke.storage, duke.profile);
        showNoteContainer();
    }

    private void showRemindersBox() {
        response = Ui.showDeadlineReminder(duke.storage, duke.profile);
        //CHECKSTYLE:OFF
        response = response.replaceAll("✓", "\u2713");
        response = response.replaceAll("✗", "\u2717");
        //CHECKSTYLE:ON
        showTaskContainer();
    }

}