package burrow.furniture.wordy;

import burrow.core.command.Command;
import burrow.core.command.CommandContext;
import burrow.furniture.hoard.HoardFurniture;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

import java.util.ArrayList;

@CommandLine.Command(
    name = "ls",
    description = "List words."
)
public class ListCommand extends Command {
    @CommandLine.Parameters(
        index = "0",
        description = "The number of words to display.",
        defaultValue = CommandLine.Option.NULL_VALUE
    )
    private Integer count;

    @CommandLine.Option(
        names = {"--tail", "-t"},
        paramLabel = "<tail>",
        description = "Whether shows latest included words.",
        defaultValue = "false"
    )
    private Boolean tail;

    public ListCommand(@NotNull final CommandContext commandContext) {
        super(commandContext);
    }

    @Override
    public Integer call() {
        var entryList = use(HoardFurniture.class).getHoard().getEntryList();
        if (tail) {
            entryList = entryList.reversed();
        }

        final var lines = new ArrayList<String>();
        final var maxLineCount =
            count != null ? Math.min(count, entryList.size()) : entryList.size();
        for (int i = 0; i < maxLineCount; i++) {
            final var entry = entryList.get(i);
            final var id = entry.getId();
            final var word = entry.getNotNull(WordyFurniture.EntryKey.WORD);
            final var translation = entry.getNotNull(WordyFurniture.EntryKey.TRANSLATION);
            lines.add("[" + id + "] " + WordCommand.getColoredWord(word) + " " +
                WordCommand.getColoredTranslation(translation));
        }

        bufferAppendLines(lines);

        return CommandLine.ExitCode.OK;
    }
}
