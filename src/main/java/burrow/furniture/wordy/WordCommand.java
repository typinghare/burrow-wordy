package burrow.furniture.wordy;

import burrow.core.command.Command;
import burrow.core.command.CommandContext;
import burrow.core.command.CommandType;
import burrow.core.common.ColorUtility;
import burrow.furniture.hoard.Entry;
import burrow.furniture.hoard.HoardFurniture;
import burrow.furniture.time.TimeFurniture;
import org.springframework.lang.NonNull;
import picocli.CommandLine;

@CommandLine.Command(
    name = "word",
    description = "Display a word entry, including the word, the translation, and other associated data."
)
@CommandType(WordyFurniture.COMMAND_TYPE)
public class WordCommand extends Command {
    @CommandLine.Parameters(index = "0", description = "The id of the word entry to display.")
    private Integer id;

    public WordCommand(@NonNull final CommandContext commandContext) {
        super(commandContext);
    }

    @Override
    public Integer call() {
        final var word = use(HoardFurniture.class).getHoard().get(id);
        displayWord(word);

        return CommandLine.ExitCode.OK;
    }

    public void displayWord(@NonNull final Entry wordEntry) {
        final var word = wordEntry.get(WordyFurniture.EntryKey.WORD);
        final var translation = wordEntry.get(WordyFurniture.EntryKey.TRANSLATION);
        final var example = wordEntry.get(WordyFurniture.EntryKey.EXAMPLE);
        final var reviews = wordEntry.get(WordyFurniture.EntryKey.REVIEWS);
        final var createdAt = wordEntry.get(TimeFurniture.EntryKey.CREATED_AT);
        final var updatedAt = wordEntry.get(TimeFurniture.EntryKey.UPDATED_AT);
        final var timeFurniture = use(TimeFurniture.class);
        final var extraInfo = "reviews: " + reviews
            + " | included at: "
            + timeFurniture.dateToString(Long.parseLong(createdAt))
            + " | reviewed at: "
            + timeFurniture.dateToString(Long.parseLong(updatedAt));

        buffer.append(ColorUtility.render(word, "green,bold"));
        buffer.append("  ");
        buffer.append(ColorUtility.render(translation, "green,bold"));
        buffer.append("\n");
        buffer.append(ColorUtility.render(example, "fg(222)"));
        buffer.append("\n");
        buffer.append(ColorUtility.render(extraInfo, "fg(247)"));
    }
}
