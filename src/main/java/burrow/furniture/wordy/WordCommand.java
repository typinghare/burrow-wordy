package burrow.furniture.wordy;

import burrow.core.command.Command;
import burrow.core.command.CommandContext;
import burrow.core.command.CommandType;
import burrow.core.common.ColorUtility;
import burrow.furniture.hoard.Entry;
import burrow.furniture.hoard.HoardFurniture;
import burrow.furniture.pair.PairFurniture;
import burrow.furniture.time.TimeFurniture;
import org.springframework.lang.NonNull;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;

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
        final var pairFurniture = use(PairFurniture.class);
        final var timeFurniture = use(TimeFurniture.class);
        final var word = pairFurniture.getKey(wordEntry);
        final var translation = pairFurniture.getValue(wordEntry);
        final var example = wordEntry.get(WordyFurniture.EntryKey.EXAMPLE);
        final var reviews = wordEntry.get(WordyFurniture.EntryKey.REVIEWS);
        final var createdAt = wordEntry.get(TimeFurniture.EntryKey.CREATED_AT);
        final var updatedAt = wordEntry.get(TimeFurniture.EntryKey.UPDATED_AT);
        final var extraInfo = "reviews: " + reviews
            + " | included at: "
            + timeFurniture.dateToString(Long.parseLong(createdAt))
            + " | reviewed at: "
            + timeFurniture.dateToString(Long.parseLong(updatedAt));
        final var environment = CommandContext.Hook.environment.getNonNull(commandContext);

        buffer.append(ColorUtility.render(word, "green,bold"));
        buffer.append("  ");
        buffer.append(ColorUtility.render(translation, "green,bold"));
        buffer.append("\n");
        buffer.append(getExampleLine(example, environment.getConsoleWidth()));
        buffer.append("\n");
        buffer.append(ColorUtility.render(extraInfo, "fg(247)"));
    }

    @NonNull
    public String getExampleLine(
        @NonNull final String example,
        @NonNull final Integer consoleWidth
    ) {
        if (consoleWidth < 20) {
            return ColorUtility.render(example, "fg(222)");
        }

        final var tokens = Arrays.stream(example.split(" ")).toList();
        final var lines = new ArrayList<String>();

        var i = 0;
        var line_number = 0;
        while (i < tokens.size()) {
            if (line_number == lines.size()) {
                lines.add("");
            }
            final var line = lines.get(line_number);

            final var token = tokens.get(i);
            if (line.isEmpty()) {
                lines.set(line_number, token);
                ++i;
            } else if (line.length() + token.length() + 1 > consoleWidth) {
                ++line_number;
            } else {
                lines.set(line_number, line + " " + token);
                ++i;
            }
        }

        return ColorUtility.render(String.join("\n", lines), "fg(222)");
    }
}
