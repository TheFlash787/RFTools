package mcjty.rftools.blocks.powercell;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.network.clientinfo.PacketGetInfoFromServer;
import mcjty.rftools.RFTools;
import mcjty.rftools.network.RFToolsMessages;
import net.minecraft.util.ResourceLocation;

import java.awt.Rectangle;

public class GuiPowerCell extends GenericGuiContainer<PowerCellTileEntity> {
    public static final int POWERCELL_WIDTH = 180;
    public static final int POWERCELL_HEIGHT = 152;

    private EnergyBar energyBar;
    private Button stats;

    private static long lastTime = 0;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFTools.MODID, "textures/gui/powercell.png");

    public GuiPowerCell(PowerCellAdvancedTileEntity te, PowerCellContainer container) {
        this((PowerCellTileEntity) te, container);
    }

    public GuiPowerCell(PowerCellNormalTileEntity te, PowerCellContainer container) {
        this((PowerCellTileEntity) te, container);
    }

    public GuiPowerCell(PowerCellSimpleTileEntity te, PowerCellContainer container) {
        this((PowerCellTileEntity) te, container);
    }

    public GuiPowerCell(PowerCellCreativeTileEntity te, PowerCellContainer container) {
        this((PowerCellTileEntity) te, container);
    }

    public GuiPowerCell(PowerCellTileEntity te, PowerCellContainer container) {
        super(RFTools.instance, RFToolsMessages.INSTANCE, te, container, RFTools.GUI_MANUAL_MAIN, "powercell");

        xSize = POWERCELL_WIDTH;
        ySize = POWERCELL_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        energyBar = new EnergyBar(mc, this).setVertical().setMaxValue(1000).setLayoutHint(10, 7, 8, 54).setShowText(false);
        energyBar.setValue(0);

        Button allNone = new Button(mc, this)
                .setName("allnone")
                .setText("None").setTooltips("Set all sides to 'none'")
                .setLayoutHint(140, 10, 32, 15);
        Button allInput = new Button(mc, this)
                .setName("allinput")
                .setText("In").setTooltips("Set all sides to", "accept energy")
                .setLayoutHint(140, 27, 32, 15);
        Button allOutput = new Button(mc, this)
                .setName("alloutput")
                .setText("Out").setTooltips("Set all sides to", "send energy")
                .setLayoutHint(140, 44, 32, 15);

        stats = new Button(mc, this)
                .setName("clearstats")
                .setText("Stats").setTooltips("Power statistics. Press to clear:")
                .setLayoutHint(100, 10, 32, 15);

        Label label = new Label(mc, this);
        label.setText("Link:").setTooltips("Link a powercard to card", "on the left").setLayoutHint(26, 30, 40, 18);

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChildren(energyBar, allNone, allInput, allOutput, label, stats);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);

        window.action(RFToolsMessages.INSTANCE, "allnone", tileEntity, PowerCellTileEntity.ACTION_SETNONE);
        window.action(RFToolsMessages.INSTANCE, "allinput", tileEntity, PowerCellTileEntity.ACTION_SETINPUT);
        window.action(RFToolsMessages.INSTANCE, "alloutput", tileEntity, PowerCellTileEntity.ACTION_SETOUTPUT);
        window.action(RFToolsMessages.INSTANCE, "clearstats", tileEntity, PowerCellTileEntity.ACTION_CLEARSTATS);

        requestRF();
    }

    private void requestRF() {
        if (System.currentTimeMillis() - lastTime > 250) {
            lastTime = System.currentTimeMillis();
            RFToolsMessages.INSTANCE.sendToServer(new PacketGetInfoFromServer(RFTools.MODID, new PowerCellInfoPacketServer(tileEntity)));
        }
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();

        requestRF();

        stats.setTooltips("Power statistics. Press to clear:", "Inserted: " + PowerCellInfoPacketClient.tooltipInserted, "Extracted: " + PowerCellInfoPacketClient.tooltipExtracted);

        int maxValue = (PowerCellInfoPacketClient.tooltipBlocks - PowerCellInfoPacketClient.tooltipAdvancedBlocks - PowerCellInfoPacketClient.tooltipSimpleBlocks) * PowerCellConfiguration.rfPerNormalCell;
        maxValue += PowerCellInfoPacketClient.tooltipAdvancedBlocks * PowerCellConfiguration.rfPerNormalCell * PowerCellConfiguration.advancedFactor;
        maxValue += PowerCellInfoPacketClient.tooltipSimpleBlocks * PowerCellConfiguration.rfPerNormalCell / PowerCellConfiguration.simpleFactor;
        energyBar.setMaxValue(maxValue);
        energyBar.setValue(PowerCellInfoPacketClient.tooltipEnergy);
    }
}
