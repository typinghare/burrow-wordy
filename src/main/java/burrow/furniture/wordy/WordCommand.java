package burrow.furniture.wordy;

import burrow.core.command.Command;
import burrow.core.command.CommandContext;
import burrow.core.command.CommandType;
import burrow.core.common.ColorUtility;
import burrow.furniture.hoard.Entry;
import burrow.furniture.hoard.HoardFurniture;
import burrow.furniture.pair.PairFurniture;
import burrow.furniture.time.TimeFurniture;
import org.jetbrains.annotations.NotNull;
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

    public WordCommand(@NotNull final CommandContext commandContext) {
        super(commandContext);
    }

    @Override
    public Integer call() {
        final var word = use(HoardFurniture.class).getHoard().get(id);
        displayWord(word);

        return CommandLine.ExitCode.OK;
    }

    public void displayWord(@NotNull final Entry wordEntry) {
        final var pairFurniture = use(PairFurniture.class);
        final var timeFurniture = use(TimeFurniture.class);
        final var word = pairFurniture.getKey(wordEntry);
        final var translation = pairFurniture.getValue(wordEntry);
        final var example = wordEntry.getNotNull(WordyFurniture.EntryKey.EXAMPLE);
        final var reviews = wordEntry.getNotNull(WordyFurniture.EntryKey.REVIEWS);
        final var createdAt = wordEntry.getNotNull(TimeFurniture.EntryKey.CREATED_AT);
        final var updatedAt = wordEntry.getNotNull(TimeFurniture.EntryKey.UPDATED_AT);
        final var extraInfo = "reviews: " + reviews
            + " | included at: "
            + timeFurniture.dateToString(Long.parseLong(createdAt))
            + " | reviewed at: "
            + timeFurniture.dateToString(Long.parseLong(updatedAt));
        final var environment = commandContext.getEnvironment();

        buffer.append(getColoredWord(word)).append("  ");
        buffer.append(getColoredTranslation(translation)).append("\n");
        buffer.append(getExampleLine(example, environment.getConsoleWidth())).append("\n");
        buffer.append(ColorUtility.render(extraInfo, "fg(247)"));
    }

    @NotNull
    public String getExampleLine(
        @NotNull final String example,
        @NotNull final Integer consoleWidth
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

    public static @NotNull String getColoredWord(@NotNull final String word) {
        return ColorUtility.render(word, "green,bold");
    }

    public static @NotNull String getColoredTranslation(@NotNull final String translation) {
        return ColorUtility.render(translation, "cyan,bold");
    }
}
