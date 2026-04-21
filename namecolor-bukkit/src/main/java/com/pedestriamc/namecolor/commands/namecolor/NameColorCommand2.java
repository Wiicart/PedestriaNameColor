package com.pedestriamc.namecolor.commands.namecolor;

import com.pedestriamc.namecolor.NameColor;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import net.wiicart.commands.command.tree.CommandTree;
import org.jetbrains.annotations.NotNull;

public class NameColorCommand2 extends CommandTree implements CartCommandExecutor {

    private final NameColor nameColor;

    public NameColorCommand2(@NotNull NameColor nameColor) {
        super(THIS, builder -> {
            builder.withChild("reset", b ->
                    b.executes(new ResetCommand(nameColor)))
                    .withChild("decorations",
                            c -> c.executes(new ResetDecorationsCommand(nameColor))
                    );
        }

        );
        this.nameColor = nameColor;
    }

    @Override
    public void onCommand(@NotNull CommandData data) {

    }

}
