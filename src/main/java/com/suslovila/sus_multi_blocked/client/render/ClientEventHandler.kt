package com.suslovila.sus_multi_blocked.client.render


import com.suslovila.sus_multi_blocked.SusMultiBlocked
import com.suslovila.sus_multi_blocked.common.item.ItemMultiBlockFormer
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getFirstBound
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getSecondBound
import com.suslovila.sus_multi_blocked.utils.SusGraphicHelper
import com.suslovila.sus_multi_blocked.utils.SusVec3
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.client.model.AdvancedModelLoader
import net.minecraftforge.client.model.IModelCustom
import org.lwjgl.opengl.GL11.*
import kotlin.math.abs

@SideOnly(Side.CLIENT)
class ClientEventHandler {
    val SELECTED_BLOCK_MODEL = ResourceLocation(SusMultiBlocked.MOD_ID, "models/shape.obj")
    val ZONE = ResourceLocation(SusMultiBlocked.MOD_ID, "models/selectedZone.obj")

    val blockSelected: IModelCustom = AdvancedModelLoader.loadModel(SELECTED_BLOCK_MODEL)
    val zone: IModelCustom = AdvancedModelLoader.loadModel(ZONE)

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun onRenderWorldLast(event: RenderWorldLastEvent) {
        val stack = Minecraft.getMinecraft()?.thePlayer?.heldItem ?: return
        if (stack.item is ItemMultiBlockFormer) {
            val firstBound = getFirstBound(stack)
            val secondBound = getSecondBound(stack)
            SusGraphicHelper.bindTexture(SusMultiBlocked.MOD_ID, "textures/models/shape.png")
            glEnable(GL_BLEND)
            glEnable(GL_ALPHA_TEST)
            firstBound?.run {
                glPushMatrix()
                SusGraphicHelper.translateFromPlayerTo(SusVec3(x, y, z).add(0.5, 0.5, 0.5), event.partialTicks)
                glScaled(0.6, 0.6, 0.6)
                glColor4f(1f, 0f, 0f, 0.5f)
                blockSelected.renderAll()
                glPopMatrix()
            }
            secondBound?.run {
                glPushMatrix()
                SusGraphicHelper.translateFromPlayerTo(SusVec3(x, y, z).add(0.5, 0.5, 0.5), event.partialTicks)
                glScaled(0.61, 0.61, 0.61)
                glColor4f(0f, 0f, 1f, 0.5f)
                blockSelected.renderAll()
                glPopMatrix()
            }
            // rendering the big zone select box
            if (firstBound == null || secondBound == null) return
            val xDelta = (secondBound.x - firstBound.x).toDouble()
            val yDelta = (secondBound.y - firstBound.y).toDouble()
            val zDelta = (secondBound.z - firstBound.z).toDouble()

            val betweenPos = SusVec3(xDelta / 2, yDelta / 2, zDelta / 2).add(
                firstBound.x.toDouble() + 0.5,
                firstBound.y.toDouble() + 0.5,
                firstBound.z.toDouble() + 0.5
            )
            glPushMatrix()
            SusGraphicHelper.translateFromPlayerTo(betweenPos, event.partialTicks)
            SusGraphicHelper.drawGuideArrows()
            glDisable(GL_CULL_FACE)
            SusGraphicHelper.bindTexture(SusMultiBlocked.MOD_ID, "textures/models/selectedZone.png")
            glPushMatrix()
            glScaled(abs(xDelta) + 1.01, abs(yDelta) + 1.01, abs(zDelta) + 1.01)
            glColor4f(1f, 1f, 1f, 0.34f)
            zone.renderAll()
            glPopMatrix()
            glScaled(abs(xDelta) + 0.99, abs(yDelta)+ 0.99, abs(zDelta)+ 0.99)
            zone.renderAll()
            glPopMatrix()
        }
    }
}
