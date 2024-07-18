package burrow.furniture.wordy;

import burrow.core.chamber.Chamber;
import burrow.core.common.Values;
import burrow.core.config.Config;
import burrow.core.furniture.BurrowFurniture;
import burrow.core.furniture.Furniture;
import burrow.furniture.hoard.Entry;
import burrow.furniture.hoard.HoardFurniture;
import burrow.furniture.pair.PairFurniture;
import burrow.furniture.time.TimeFurniture;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import java.util.Random;

@BurrowFurniture(
    simpleName = "Wordy",
    description = "Learn and review vocabulary with Wordy!",
    type = BurrowFurniture.Type.MAIN,
    dependencies = {
        PairFurniture.class,
        TimeFurniture.class
    }
)
public class WordyFurniture extends Furniture {
    public static final String COMMAND_TYPE = "Wordy";

    private final Random random = new Random();
    private int lastWordId = 0;

    public WordyFurniture(@NotNull final Chamber chamber) {
        super(chamber);
    }

    @Override
    public void initializeConfig(@NotNull final Config config) {
        config.set(PairFurniture.ConfigKey.PAIR_KEY_NAME, EntryKey.WORD);
        config.set(PairFurniture.ConfigKey.PAIR_VALUE_NAME, EntryKey.TRANSLATION);
    }

    @Override
    public void beforeInitialization() {
        registerCommand(NewCommand.class);
        registerCommand(WordCommand.class);
        registerCommand(NextCommand.class);
        registerCommand(ArchiveCommand.class);
        registerCommand(ArchiveLastCommand.class);
        registerCommand(ListCommand.class);
    }

    @Nullable
    public Entry getNextWord() {
        final var hoard = use(HoardFurniture.class).getHoard();
        if (hoard.getSize() == 0) {
            return null;
        }

        final var allEntries =
            hoard.getEntryList().stream().filter(entry -> entry.isFalse(EntryKey.IS_ARCHIVED))
                .toList();
        final var id = random.nextInt(allEntries.size());
        final var wordEntry = allEntries.get(id);

        // Update reviews and time
        wordEntry.set(EntryKey.REVIEWS, wordEntry.getInt(EntryKey.REVIEWS, 0) + 1);
        use(TimeFurniture.class).setUpdateTime(wordEntry);

        // Save the id of the entry
        lastWordId = wordEntry.getId();

        return wordEntry;
    }

    public int getLastWordId() {
        return lastWordId;
    }

    @NotNull
    public Entry createWordEntry(
        @NotNull final String word,
        @NotNull final String translation
    ) {
        final var pairFurniture = use(PairFurniture.class);
        final var entry = pairFurniture.createEntryWithKeyValue(word, translation);
        entry.set(EntryKey.EXAMPLE, Values.EMPTY);
        entry.set(EntryKey.IS_ARCHIVED, Values.Bool.FALSE);
        entry.set(EntryKey.REVIEWS, Values.Int.ZERO);

        return entry;
    }

    public void setExample(@NotNull final Entry entry, @NotNull final String example) {
        entry.set(EntryKey.EXAMPLE, example);
    }

    public void archive(@NotNull final Entry entry) {
        entry.set(EntryKey.IS_ARCHIVED, Values.Bool.TRUE);
        use(TimeFurniture.class).setUpdateTime(entry);
    }

    public void archiveLastWord() {
        final var hoard = use(HoardFurniture.class).getHoard();
        final var lastWordId = getLastWordId();
        if (!hoard.exist(lastWordId)) {
            return;
        }

        final var lastWord = hoard.get(lastWordId);
        archive(lastWord);
    }

    public @interface EntryKey {
        String WORD = "word";
        String TRANSLATION = "translation";
        String EXAMPLE = "example";
        String IS_ARCHIVED = "is_archived";
        String REVIEWS = "reviews";
    }
}
