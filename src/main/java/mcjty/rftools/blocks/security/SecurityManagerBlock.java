package mcjty.rftools.blocks.security;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.tools.ItemStackTools;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.GenericRFToolsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class SecurityManagerBlock extends GenericRFToolsBlock<SecurityManagerTileEntity, SecurityManagerContainer> {

    public SecurityManagerBlock() {
        super(Material.IRON, SecurityManagerTileEntity.class, SecurityManagerContainer.class, "security_manager", true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiSecurityManager.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            NBTTagList bufferTagList = tagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);

            int rc = 0;
            for (int i = 0 ; i < bufferTagList.tagCount() ; i++) {
                NBTTagCompound itemTag = bufferTagList.getCompoundTagAt(i);
                if (itemTag != null) {
                    ItemStack stack = ItemStackTools.loadFromNBT(itemTag);
                    if (ItemStackTools.isValid(stack)) {
                        rc++;
                    }
                }
            }

            list.add(TextFormatting.GREEN + "Contents: " + rc + " stacks");
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "This block manages your security cards");
        } else {
            list.add(TextFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @Override
    public int getGuiID() {
        return RFTools.GUI_SECURITY_MANAGER;
    }
}
