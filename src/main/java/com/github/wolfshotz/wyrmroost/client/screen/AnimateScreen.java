package com.github.wolfshotz.wyrmroost.client.screen;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.TextComponent;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnimateScreen extends Screen
{
    public static AnimateScreen last;

    public final TameableDragonEntity dragon;
    private final Map<String, ModelPart> boxes;
    private final List<TransformationWidget> transformations = new ArrayList<>();
    private ReloadWidget reloader;
    private EditBox boxAdder;
    private String error;

    protected AnimateScreen(TameableDragonEntity dragon)
    {
        super(new TextComponent("Debug: Animate Screen"));

        this.dragon = dragon;
        this.boxes = referBoxList();
    }

    @Override
    protected void init()
    {
        super.init();
        transformations.forEach(TransformationWidget::init);
        if (reloader != null) reloader = new ReloadWidget();
        error = null;

        addRenderableWidget(new Button(0, 0, 33, 20, new TextComponent("Clear"), b ->
        {
            onClose();
            last = null;
        }));
        addRenderableWidget(new Button(34, 0, 20, 20, new TextComponent("+"), b -> addTransformer(true), (b, m, x, y) -> renderTooltip(m, new TextComponent("Add Rotation Box"), x, y)));
        addRenderableWidget(new Button(55, 0, 20, 20, new TextComponent("<+>"), b -> addTransformer(false), (b, m, x, y) -> renderTooltip(m, new TextComponent("Add Position Box"), x, y)));
        addRenderableWidget(new Button(77, 0, 80, 20, new TextComponent("Print Positions"), b -> printPositions()));
        addRenderableWidget(new Button(160, 0, 90, 20, new TextComponent("Reload Positions"), b ->
        {
            if (reloader == null) addWidget(reloader = new ReloadWidget());
        }));

        addWidget(boxAdder = new EditBox(font, 1, 22, 100, 9, new TextComponent("")));
    }

    private void printPositions()
    {
        if (!transformations.isEmpty())
        {
            StringBuilder builder = new StringBuilder("Printing Positions");
            for (TransformationWidget w : transformations)
            {
                if (w.xT == 0 && w.yT == 0 && w.zT == 0) continue;
                builder.append("\n").append(w.getOutput());
            }
            System.out.println(builder);
        }
    }

    private Map<String, ModelPart> referBoxList()
    {
        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dragon);
        if (renderer instanceof LivingEntityRenderer<?, ?>)
        {
            EntityModel<?> model = ((LivingEntityRenderer<?, ?>) renderer).getModel();
            return Arrays.stream(model.getClass().getFields())
                    .filter(f -> ModelPart.class.isAssignableFrom(f.getType()))
                    .collect(Collectors.toMap(Field::getName, f ->
                    {
                        try
                        {
                            return (ModelPart) f.get(model);
                        }
                        catch (Throwable e)
                        {
                            throw new RuntimeException(e);
                        }
                    }));
        }
        return null;
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        super.render(ms, mouseX, mouseY, partialTicks);
        transformations.forEach(b -> b.render(ms, mouseX, mouseY, partialTicks));
        boxAdder.render(ms, mouseX, mouseY, partialTicks);
        if (reloader != null) reloader.render(ms, mouseX, mouseY, partialTicks);
        font.drawShadow(ms, error, 105, 22, 0xFF0000);
    }

    @Override
    public boolean mouseScrolled(double width, double height, double amount)
    {
        for (TransformationWidget w : transformations) w.setWidgetY(w.y + ((int) amount * 3));
        return super.mouseScrolled(width, height, amount);
    }

    @Override
    public void onClose()
    {
        super.onClose();
        printPositions();
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public void positionModel()
    {
        transformations.forEach(TransformationWidget::patchBox);
    }

    public void addTransformer(boolean rotate)
    {
        String name = boxAdder.getValue();
        ModelPart box = boxes.get(name);
        if (box == null)
        {
            error = "No box found for the name: \"" + name + "\"";
            return;
        }
        else error = null;

        transformations.add(new TransformationWidget(name, box, rotate));
    }

    public static void open(TameableDragonEntity dragon)
    {
        if (last == null || last.dragon != dragon) last = new AnimateScreen(dragon);
        else last.init();
        Minecraft.getInstance().setScreen(last);
    }

    public class TransformationWidget extends EditBox
    {
        private final String name;
        private final ModelPart box;
        private Button closeButton;
        private final boolean rotate;
        private float xT, yT, zT;

        public TransformationWidget(String name, ModelPart box, boolean rotateOrMove)
        {
            super(font, 60, 0, 90, 13, new TextComponent(""));
            this.box = box;
            this.name = name;
            this.rotate = rotateOrMove;

            setValue("0, 0, 0");
            setResponder(this::updateCords);
            setTextColor(rotateOrMove? 0xFFFFFF : 0x00FFFF);
            init();
        }

        public TransformationWidget(String name, String input, boolean rotateOrMove)
        {
            this(name, boxes.get(name), rotateOrMove);
            updateCords(input);
            setValue(String.format("%s, %s, %s", xT, yT, zT));
        }

        public void init()
        {
            addRenderableWidget(closeButton = new Button(x + 92, y, 13, 13, new TextComponent("X"), b ->
            {
                onClose();
                int i = transformations.indexOf(this);
                transformations.remove(this);
                transformations.subList(i, transformations.size()).forEach(TransformationWidget::reposition);
            }));

            reposition();

            addWidget(this);
        }

        public void reposition()
        {
            int i = transformations.indexOf(this);
            if (transformations.isEmpty() || i == 0) setWidgetY(35);
            else
            {
                if (i == -1) i = transformations.size();
                setWidgetY(transformations.get(i - 1).y + 14);
            }
        }

        /*public void close()
        {
            children.remove(this);
            buttons.remove(closeButton);
            children.remove(closeButton);
        }*/

        @Override
        public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks)
        {
            closeButton.visible = visible;
            super.render(ms, mouseX, mouseY, partialTicks);
        }

        @Override
        public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks)
        {
            super.renderButton(ms, mouseX, mouseY, partialTicks);
            font.drawShadow(ms, name, 1, y + 3f, 0xFFFFFF);
        }

        public void setWidgetY(int y)
        {
            this.y = y;
            closeButton.y = y;

            visible = y > 31 || y > AnimateScreen.this.height;
        }

        public void updateCords(String text)
        {
            try
            {
                String[] coords = text.trim().split(",", 3);
                xT = Float.parseFloat(coords[0]);
                yT = Float.parseFloat(coords[1]);
                zT = Float.parseFloat(coords[2]);
                error = null;
            }
            catch (Throwable e)
            {
                error = "Invalid cords received for: \"" + name + "\"";
            }
        }

        public void patchBox()
        {
            if (rotate)
            {
                box.xRot += xT;
                box.yRot += yT;
                box.zRot += zT;
            }
            else
            {
                box.x += xT;
                box.y += yT;
                box.z += zT;
            }
        }

        public String getOutput()
        {
            return String.format("%s(%s, %sf, %sf, %sf);", rotate? "rotate" : "move", name, xT, yT, zT);
        }
    }

    public class ReloadWidget extends EditBox
    {
        private Button closeButton;
        private Button parseButton;
        private Button pathButton;

        public ReloadWidget()
        {
            super(font, AnimateScreen.this.width / 2 - 100, AnimateScreen.this.height / 2 - 22, 200, 45, new TextComponent(""));
            setValue("Insert Path to file...");
            setMaxLength(Integer.MAX_VALUE);
            init();
        }

        public void init()
        {
            addRenderableWidget(closeButton = new Button(x + width + 5, y + 1, 20, 20, new TextComponent("X"), b -> onClose()));
            addRenderableWidget(parseButton = new Button(x + width / 2 - 40, y + height + 10, 80, 20, new TextComponent("Parse"), b -> parseAndReload()));
            addRenderableWidget(pathButton = new Button(x + width + 5, y + 23, 60, 20, new TextComponent("Get Path"), b -> setValue(System.getProperty("user.home") + "/Desktop/")));
        }

        private void parseAndReload()
        {
            try
            {
                for (String line : Files.readAllLines(Paths.get(getValue())))
                {
                    line = line.trim();
                    try
                    {
                        int para = line.indexOf("(");
                        String rotateOrMove = line.substring(0, para);
                        String substring = line.substring(para + 1, line.indexOf(")") - 1);
                        String[] split = substring.split(",", 2);
                        transformations.add(new TransformationWidget(split[0], split[1], !rotateOrMove.equals("move")));
                    }
                    catch (StringIndexOutOfBoundsException e)
                    {
                        Wyrmroost.LOG.error("Invalid line: " + line);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Wyrmroost.LOG.error("Invalid line has too few arguments: " + line);
                    }
                }
                error = null;
                onClose();
            }
            catch (NoSuchFileException e)
            {
                e.printStackTrace();
                error = "ayo try actually supplying a real file.";
            }
            catch (Exception e)
            {
                e.printStackTrace();
                error = "Something went horrible wrong trying to parse this object; check logs.";
            }
        }

        /*public void close()
        {
            children.remove(parseButton);
            children.remove(closeButton);
            children.remove(pathButton);
            buttons.remove(parseButton);
            buttons.remove(closeButton);
            buttons.remove(pathButton);
            children.remove(this);
            reloader = null;
        }*/
    }
}
