package com.github.tymefly.common.document.visitor.white;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.VisitorKey;

/**
 * A Visitor that will create a copy of a Document with some white list filters.
 * This can be used to validate Documents
 */
@NotThreadSafe
public class WhiteList implements DocumentVisitor<Document> {
    private record Pair (@Nonnull DocumentKey key, @Nonnull List<DocumentKey> children) {
    }


    /**
     * A builder class that creates a {@link WhiteList} DocumentVisitor.
     * As white lists are typically reused, but as WhiteList objects can not be reused normal usage is to configure
     * a Builder once and then create a new instances of the WhiteList for each Document
     */
    @ThreadSafe
    public static class Builder implements FluentRequiredCheck, FluentCheck, FluentFailure {
        // These functions must be the last in the chain.
        private static final Consumer<DocumentKey> DO_NOTHING = badKey -> {};
        private static final Consumer<DocumentKey> FAIL_HANDLER = badKey -> {
            throw new WhiteListException(badKey);
        };

        private final Map<String, List<WhiteItem>> whiteItems = new HashMap<>();
        private final List<Pair> childChecks = new ArrayList<>();
        private final Set<String> required = new HashSet<>();
        private Consumer<DocumentKey> failHandler = badKey -> {};
        private Consumer<DocumentKey> exceptionHandler = DO_NOTHING;
        private boolean allowNull = false;
        private boolean require = false;


        /**
         * Checks that the {@code key} contains all of the {@code children}
         * @param key           A key in the document that is being validated
         * @param children      The minimum set of required children under {@code key}
         * @return  A fluent interface
         */
        @Nonnull
        public Builder forChildren(@Nonnull DocumentKey key, DocumentKey... children) {
            Pair check = new Pair(key, List.of(children));
            this.childChecks.add(check);

            return this;
        }

        /**
         * If called as part of the fluid interface, the next check will define a field that is required
         * in the document that is tested. This is only meaningful if a failure action is defined.
         * If this method is not called then the following field is optional.
         * @return  A fluent interface
         * @see FluentFailure
         */
        @Nonnull
        public FluentRequiredCheck require() {
            require = true;

            return this;
        }

        @Override
        @Nonnull
        public FluentCheck allowNull() {
            allowNull = true;

            return this;
        }

        @Override
        @Nonnull
        public Builder forLength(@Nonnull DocumentKey key, int maxLength) {
            return forLength(key, 0, maxLength);
        }

        @Nonnull
        @Override
        public Builder forLength(@Nonnull DocumentKey key, int minLength, int maxLength) {
            return add(key, new StringLength(minLength, maxLength, allowNull));
        }

        @Override
        @Nonnull
        public Builder forRegEx(@Nonnull DocumentKey key, @Nonnull String regEx) {
            return add(key, new RegExItem(regEx, allowNull));
        }

        @Nonnull
        @Override
        public Builder forCardinal(@Nonnull DocumentKey key) {
            return forRegEx(key, "\\d+");
        }

        @Nonnull
        @Override
        public Builder forInteger(@Nonnull DocumentKey key) {
            return forRegEx(key, "-?\\d+");
        }

        @Override
        @Nonnull
        public Builder forDecimal(@Nonnull DocumentKey key) {
            return forRegEx(key, "-?\\d+(\\.\\d+)?");
        }

        @Nonnull
        @Override
        public Builder forRange(@Nonnull DocumentKey key, long maximum) {
            return add(key, new RangeItem(0, maximum, allowNull));
        }

        @Override
        @Nonnull
        public Builder forRange(@Nonnull DocumentKey key, long minimum, long maximum) {
            return add(key, new RangeItem(minimum, maximum, allowNull));
        }

        @Nonnull
        @Override
        public Builder forBoolean(@Nonnull DocumentKey key) {
            return add(key, new BooleanItem(allowNull));
        }

        @Nonnull
        @Override
        public <E extends Enum<E>> Builder forEnum(@Nonnull DocumentKey key, @Nonnull Class<E> type) {
            return add(key, new EnumItem<>(type, allowNull));
        }

        @Nonnull
        @Override
        public Builder forCheck(@Nonnull DocumentKey key, @Nonnull Predicate<? super Object> test) {
            return add(key, new CheckItem(test, allowNull));
        }


        @Nonnull
        private Builder add(@Nonnull DocumentKey key, @Nonnull WhiteItem white) {
            String externalised = key.externalise();

            whiteItems.computeIfAbsent(externalised, k -> new ArrayList<>())
                .add(white);

            if (require) {
                required.add(externalised);
                require = false;
            }

            allowNull = false;

            return this;
        }

        @Override
        @Nonnull
        public FluentFailure onFail() {
            exceptionHandler = FAIL_HANDLER;

            return this;
        }

        @Override
        @Nonnull
        public FluentFailure onFail(@Nonnull Consumer<DocumentKey> handler) {
            this.failHandler = this.failHandler.andThen(handler);

            return this;
        }

        @Override
        @Nonnull
        public WhiteList build() {
            onFail(exceptionHandler);

            return new WhiteList(this);
        }
    }


    private final Map<String, List<WhiteItem>> whiteItems;
    private final List<Pair> childChecks;
    private final Set<String> required;
    private final Consumer<DocumentKey> failHandler;
    private final Document result;
    private final Set<DocumentKey> remove;                 // Keys that needs to be removed from result
    private Class<?> type = String.class;


    private WhiteList(@Nonnull Builder builder) {
        this.whiteItems = Map.copyOf(builder.whiteItems);
        this.childChecks = List.copyOf(builder.childChecks);
        this.required = new HashSet<>(builder.required);
        this.failHandler = builder.failHandler;
        this.result = Document.newInstance();
        this.remove = new HashSet<>();
    }

    @Nonnull
    @Override
    public WhiteList nullValue(@Nonnull VisitorKey key) {
        return testField(key, null, type);
    }

    @Nonnull
    @Override
    public WhiteList stringValue(@Nonnull VisitorKey key, @Nonnull String value) {
        return testField(key, value, String.class);
    }

    @Nonnull
    @Override
    public WhiteList numericValue(@Nonnull VisitorKey key, @Nonnull Number value) {
        return testField(key, value, Number.class);
    }

    @Nonnull
    @Override
    public WhiteList booleanValue(@Nonnull VisitorKey key, boolean value) {
        return testField(key, value, Boolean.class);
    }

    @Nonnull
    @Override
    public WhiteList enumValue(@Nonnull VisitorKey key, @Nonnull Enum<?> value) {
        return testField(key, value, Enum.class);
    }

    @Nonnull
    @Override
    public WhiteList beginChild(@Nonnull VisitorKey key) {
        return this;
    }

    @Nonnull
    @Override
    public WhiteList endChild(@Nonnull VisitorKey key) {
        return this;
    }

    @Nonnull
    @Override
    public WhiteList beginSequence(@Nonnull VisitorKey key, @Nonnull Class<?> type, int size) {
        this.type = type;

        return this;
    }

    @Nonnull
    @Override
    public WhiteList endSequence(@Nonnull VisitorKey key) {
        this.type = String.class;

        return this;
    }

    @Nonnull
    @Override
    public Document process() {
        if (!required.isEmpty()) {
            failHandler.accept(() -> required.iterator().next());
        }

        requireChildren();

        for (var key : remove) {
            result.remove(key);
        }

        return result;
    }

    /**
     * {@link #whiteItems} checks.
     * @param key       Name of field to check
     * @param value     Value associated with {@code key}
     * @param type      Type of {@code value}. This is required in the case we add a {@code null} to the start of
     *                      an array. For non-array elements we any type will do
     * @return          {@code this} object for a fluent interface
     */
    @Nonnull
    private WhiteList testField(@Nonnull VisitorKey key, @Nullable Object value, @Nonnull Class<?> type) {
        String pattern = key.fullPath().replaceAll("\\[\\d+]", "");
        List<WhiteItem> rules = whiteItems.get(pattern);
        boolean valid = false;

        if (rules != null) {
            for (var whiteItem : rules) {
                valid = (value == null ? whiteItem.allowNull() : whiteItem.validate(value));

                if (!valid) {
                    if (value == null) {
                        remove.add(() -> pattern);
                    }

                    break;
                }
            }
        }

        if (!valid) {
            failHandler.accept(key.documentKey());
        } else if (type == String.class) {
            result.addString(key.documentKey(), (String) value);
        } else if (type == Number.class) {
            result.addNumber(key.documentKey(), (Number) value);
        } else if (type == Boolean.class) {
            result.addBoolean(key.documentKey(), (Boolean) value);
        } else if (type == Enum.class) {
            result.addEnum(key.documentKey(), (Enum<?>) value);
        } else {
            throw new DocumentException("INTERNAL ERROR: Unexpected sequence type %s", type.getSimpleName());
        }

        required.remove(key.simpleKeyPath());

        return this;
    }


    /**
     * filter based on {@link #childChecks}
     */
    private void requireChildren() {
        for (var check : childChecks) {
            DocumentKey key = check.key();
            List<DocumentKey> required = check.children();
            List<? extends Document> test = result.getAll(key, Document.class);
            int index = 0;

            for (var child : test) {
                requireChildren(key, child, index++, required);
            }
        }
    }


    private void requireChildren(@Nonnull DocumentKey rootKey,
                                 @Nullable Document root,
                                 int index,
                                 @Nonnull List<DocumentKey> required) {
        boolean valid = (root != null);
        String name = "";

        if (valid) {
            for (var childKey : required) {
                valid = root.contains(childKey);

                if (!valid) {
                    name = childKey.externalise();
                    break;
                }
            }
        }

        if (!valid) {
            String childPath = rootKey.externalise() + '[' + index + ']';
            DocumentKey childKey = () -> childPath;
            String finalName = name;
            DocumentKey errorKey = (name.isEmpty() ? childKey : () -> childPath + DocumentKey.SEPARATOR + finalName);

            failHandler.accept(errorKey);
            remove.add(childKey);
        }
    }
}
