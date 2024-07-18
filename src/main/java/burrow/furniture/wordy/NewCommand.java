package burrow.furniture.wordy;

import burrow.core.command.Command;
import burrow.core.command.CommandContext;
import burrow.core.command.CommandType;
import burrow.furniture.hoard.HoardFurniture;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

@CommandLine.Command(
    name = "new",
    description = "Include a new word."
)
@CommandType()
public class NewCommand extends Command {
    @CommandLine.Parameters(index = "0", description = "The original word.")
    private String word;

    @CommandLine.Parameters(index = "1", description = "The translation of the word.")
    private String translation;

    @CommandLine.Parameters(index = "2", description = "Example sentence.", defaultValue = "")
    private String example;

    public NewCommand(@NotNull final CommandContext commandContext) {
        super(commandContext);
    }

    @Override
    public Integer call() {
        final var wordyFurniture = use(WordyFurniture.class);
        final var entry = wordyFurniture.createWordEntry(word, translation);
        if (!example.isEmpty()) {
            wordyFurniture.setExample(entry, example);
        }

        buffer.append(use(HoardFurniture.class).entryToString(entry, commandContext));

        return CommandLine.ExitCode.OK;
    }
}
