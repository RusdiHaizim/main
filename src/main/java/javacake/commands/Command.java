package javacake.commands;

import javacake.*;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Command {
    protected CmdType type;
    protected String input;

    /**
     * Types of commands that are possible.
     */
    public enum CmdType {
        EXIT, LIST, FIND, DONE, DELETE, TODO, DEADLINE, EVENT, REMIND, FREETIME, VIEWSCH, TENTATIVE, CONFIRM,
        EDIT, BACK, GOTO

    }

    public abstract void execute(ProgressStack progressStack, Ui ui, Storage storage, Profile profile) throws DukeException, IOException;

    /**
     * Method to get the type of command.
     * @return Type of command
     */
    public CmdType type() {
        return type;
    }

    /**
     * Method to get the input inside the command.
     * @return String containing user input in command
     */
    public String input() {
        return input;
    }

    /**
     * Method to check whether command is of type exit.
     * @return true if type is exit, false otherwise
     */
    public boolean isExit() {
        return this.type == CmdType.EXIT;
    }
}