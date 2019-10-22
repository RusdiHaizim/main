package javacake.commands;

import javacake.Duke;
import javacake.exceptions.DukeException;
import javacake.storage.Profile;
import javacake.ProgressStack;
import javacake.storage.Storage;
import javacake.ui.Ui;
import javacake.quiz.Question;

public class GoToCommand extends Command {

    private String index;

    public GoToCommand(String inputCommand) {
        String[] buffer = inputCommand.split("\\s+");
        index = buffer[1];
    }

    /**
     * Execute jumping to given index.
     * @param progressStack tracks current location in program
     * @param ui the Ui responsible for outputting messages
     * @param storage Storage needed to write the updated data
     * @param profile Profile of the user
     * @throws DukeException Error thrown when unable to close reader
     */
    public String execute(ProgressStack progressStack, Ui ui, Storage storage, Profile profile) throws DukeException {
        int intIndex = Integer.parseInt(index) - 1;
        progressStack.updateFilePath(progressStack.gotoFilePath(intIndex));
        String filePath = progressStack.getFullFilePath();
        if (filePath.contains("Quiz")) {
            if (filePath.contains("1. Java Basics")) {
                if (Duke.isCliMode()) {
                    return new QuizCommand(Question.QuestionType.BASIC)
                            .execute(progressStack, ui, storage, profile);
                } else {
                    QuizCommand.setProfile(profile);
                    return "!@#_QUIZ_1";
                }
            } else if (filePath.contains("2. Object-Oriented Programming")) {
                if (Duke.isCliMode()) {
                    return new QuizCommand(Question.QuestionType.OOP)
                            .execute(progressStack, ui, storage, profile);
                } else {
                    QuizCommand.setProfile(profile);
                    return "!@#_QUIZ_2";
                }
            } else if (filePath.contains("3. Extensions")) {
                if (Duke.isCliMode()) {
                    return new QuizCommand(Question.QuestionType.EXTENSIONS)
                            .execute(progressStack, ui, storage, profile);
                } else {
                    QuizCommand.setProfile(profile);
                    return "!@#_QUIZ_3";
                }
            } else {
                if (Duke.isCliMode()) {
                    return new QuizCommand(Question.QuestionType.ALL)
                            .execute(progressStack, ui, storage, profile);
                } else {
                    QuizCommand.setProfile(profile);
                    return "!@#_QUIZ_4";
                }
            }
        }
        progressStack.insertQueries();
        if (progressStack.containsDirectory()) {
            return (progressStack.displayDirectories());
        } else {
            progressStack.updateFilePath(progressStack.gotoFilePath(0));
            return (progressStack.readQuery());
        }
    }
}
