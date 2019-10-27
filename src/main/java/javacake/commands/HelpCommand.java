package javacake.commands;

import javacake.Logic;
import javacake.exceptions.DukeException;
import javacake.storage.Profile;
import javacake.storage.Storage;
import javacake.ui.Ui;

public class HelpCommand extends Command {

    private StringBuilder stringBuilder = new StringBuilder();

    /**
     * Constructor for HelpCommand.
     * @param str Input command from user.
     */
    public HelpCommand(String str) {
        type = CmdType.HELP;
        input = str;
    }

    /**
     * Execute S.O.S.
     * @param logic tracks current location in program
     * @param ui the Ui responsible for outputting messages
     * @param storage Storage needed to write the updated data
     * @param profile Profile of the user
     * @throws DukeException Error thrown when unable to close reader
     * @return method for the related help requested.
     */
    @Override
    public String execute(Logic logic, Ui ui, Storage storage, Profile profile) throws DukeException {
        switch (input) {
            case "help" : return mainHelp();
            case "help back" : return exitHelp();
            case "help goto": return gotoHelp();
            case "help help": return helpHelp();
            case "help list": return listHelp();
            case "help score": return scoreHelp();
            case "help overview": return overviewHelp();
            case "help createnote": return createNoteHelp();
            case "help editnote": return editNoteHelp();
            case "help listnote": return listNoteHelp();
            default: throw new DukeException("No such command found. Try again.");
        }
    }

    /**
     * Displays list of built-in commands.
     * @return List of commands that users can execute.
     */
    private String mainHelp() {
        stringBuilder.append("Here is the list of available commands:\n\n");
        stringBuilder.append("- ").append("back").append("\n");
        stringBuilder.append("- ").append("exit").append("\n");
        stringBuilder.append("- ").append("goto 'X' [X = {1,2,..}]").append("\n");
        stringBuilder.append("- ").append("help").append("\n");
        stringBuilder.append("- ").append("list").append("\n");
        stringBuilder.append("- ").append("overview").append("\n");
        stringBuilder.append("- ").append("score").append("\n");
        stringBuilder.append("- ").append("createnote").append("\n");
        stringBuilder.append("- ").append("editnote").append("\n");
        stringBuilder.append("- ").append("listnote").append("\n");
        stringBuilder.append("\nType 'help COMMAND_NAME' to learn more in-depth!\n");
        return stringBuilder.toString();
    }

    /**
     * Provides a brief explanation on how back command work.
     * @return String of instructions to guide user on back command.
     */
    private String backHelp() {
        stringBuilder.append("- ").append("back").append("\n");
        stringBuilder.append("Command to jump back to previous state after calling 'goto X'").append("\n");
        return stringBuilder.toString();
    }

    /**
     * Provides a brief explanation on how exit command work.
     * @return String of instructions to guide user on exit command.
     */
    private String exitHelp() {
        stringBuilder.append("- ").append("exit").append("\n");
        stringBuilder.append("Command to exit from the program").append("\n");
        return stringBuilder.toString();
    }

    /**
     * Provides a brief explanation on how goto command work.
     * @return String of instructions to guide user on goto command.
     */
    private String gotoHelp() {
        stringBuilder.append("Command to jump to next index of content!").append("\n");
        stringBuilder.append("- ").append("Simply type 'goto [index]' where [index] is the index you wish to view.");
        stringBuilder.append("\n");
        stringBuilder.append("- ").append("E.g. goto '1' will bring you to the content in index 1.").append("\n");
        stringBuilder.append("- ").append("E.g. goto '1.2' will bring you to ");
        stringBuilder.append("- ").append("index 1 followed by 2!").append("\n");
        stringBuilder.append("- ").append("You get the point!").append("\n");
        return stringBuilder.toString();
    }

    /**
     * Provides a brief explanation on how help command work.
     * @return String of instructions to guide user on help command.
     */
    private String helpHelp() {
        stringBuilder.append("- ").append("help").append("\n");
        stringBuilder.append("I am trying to help you!!!").append("\n");
        return stringBuilder.toString();
    }

    /**
     * Provides a brief explanation on how list command work.
     * @return String of instructions to guide user on list command.
     */
    private String listHelp() {
        stringBuilder.append("- ").append("list").append("\n");
        stringBuilder.append("Command to display table-of-contents").append("\n");
        return stringBuilder.toString();
    }

    /**
     * Provides a brief explanation on how score command work.
     * @return String of instructions to guide user on score command.
     */
    private String scoreHelp() {
        stringBuilder.append("- ").append("score").append("\n");
        stringBuilder.append("Command to get your current overall score for JavaCake!").append("\n");
        return stringBuilder.toString();
    }

    /**
     * Provides a brief explanation on how overview command work.
     * @return String of instructions to guide user on overview command.
     */
    private String overviewHelp() {
        stringBuilder.append("- ").append("overview").append("\n");
        stringBuilder.append("Command to view all the content installed in JavaCake!").append("\n");
        return stringBuilder.toString();
    }

    /**
     * Provides a brief explanation on how createnote command work.
     * @return String of instructions to guide user on createnote command.
     */
    private String createNoteHelp() {
        stringBuilder.append("- ").append("createnote").append("\n");
        stringBuilder.append("Command to create your own notes in JavaCake!").append("\n");
        stringBuilder.append("Simply type in 'createnote' or 'createnote [desired name of note]'!").append("\n");
        stringBuilder.append("And please keep your file name in alphanumeric to avoid illegal characters!")
                .append("\n");
        return stringBuilder.toString();
    }

    /**
     * Provides a brief explanation on how editnote command work.
     * @return String of instructions to guide user on editnote command.
     */
    private String editNoteHelp() {
        stringBuilder.append("- ").append("editnote").append("\n");
        stringBuilder.append("Command to edit the pre-existing note created by you!").append("\n");
        stringBuilder.append("View the notes available on the right window!").append("\n");
        stringBuilder.append("Simply type in 'editnote [name of note]' to edit!").append("\n");
        stringBuilder.append("Remember '.txt' must be omitted!").append("\n");
        return stringBuilder.toString();
    }

    /**
     * Provides a brief explanation on how listnote command work.
     * @return String of instructions to guide user on listnote command.
     */
    private String listNoteHelp() {
        stringBuilder.append("- ").append("listnote").append("\n");
        stringBuilder.append("Command to list all the pre-existing note created by you!").append("\n");
        stringBuilder.append("Simply type in 'listnote' to refresh the list of notes!").append("\n");
        return stringBuilder.toString();
    }

}
