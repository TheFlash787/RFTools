package mcjty.rftools.shapes;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.TypedMapTools;
import mcjty.lib.thirteen.Context;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

/**
 * This is a packet that can be used to update the NBT on the held item of a player.
 */
public class PacketUpdateNBTShapeCard implements IMessage {
    private TypedMap args;

    @Override
    public void fromBytes(ByteBuf buf) {
        args = TypedMapTools.readArguments(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        TypedMapTools.writeArguments(buf, args);
    }

    public PacketUpdateNBTShapeCard() {
    }

    public PacketUpdateNBTShapeCard(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketUpdateNBTShapeCard(TypedMap arguments) {
        this.args = arguments;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            EntityPlayerMP playerEntity = ctx.getSender();
            ItemStack heldItem = playerEntity.getHeldItem(EnumHand.MAIN_HAND);
            if (heldItem.isEmpty()) {
                return;
            }
            NBTTagCompound tagCompound = heldItem.getTagCompound();
            if (tagCompound == null) {
                tagCompound = new NBTTagCompound();
                heldItem.setTagCompound(tagCompound);
            }
            for (Key<?> akey : args.getKeys()) {
                String key = akey.getName();
                if (Type.STRING.equals(akey.getType())) {
                    tagCompound.setString(key, (String) args.get(akey));
                } else if (Type.INTEGER.equals(akey.getType())) {
                    tagCompound.setInteger(key, (Integer) args.get(akey));
                } else if (Type.DOUBLE.equals(akey.getType())) {
                    tagCompound.setDouble(key, (Double) args.get(akey));
                } else if (Type.BOOLEAN.equals(akey.getType())) {
                    tagCompound.setBoolean(key, (Boolean) args.get(akey));
                } else if (Type.BLOCKPOS.equals(akey.getType())) {
                    throw new RuntimeException("BlockPos not supported for PacketUpdateNBTItem!");
                } else if (Type.ITEMSTACK.equals(akey.getType())) {
                    throw new RuntimeException("ItemStack not supported for PacketUpdateNBTItem!");
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}