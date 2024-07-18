package burrow.furniture.wordy;

import burrow.core.command.Command;
import burrow.core.command.CommandContext;
import burrow.core.command.CommandType;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

@CommandLine.Command(name = "next", description = "Display a random next word.")
@CommandType(WordyFurniture.COMMAND_TYPE)
public class NextCommand extends Command {
    public NextCommand(@NotNull final CommandContext commandContext) {
        super(commandContext);
    }

    @Override
    public Integer call() throws NoSuchFieldException, IllegalAccessException {
        final var wordyFurniture = use(WordyFurniture.class);
        final var nextWord = wordyFurniture.getNextWord();
        if (nextWord == null) {
            buffer.append("No available words.");
        } else {
            final var wordCommand = new WordCommand(commandContext);
            final var idField = wordCommand.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(wordCommand, nextWord.getId());
            return wordCommand.call();
        }

        return CommandLine.ExitCode.OK;
    }
}
