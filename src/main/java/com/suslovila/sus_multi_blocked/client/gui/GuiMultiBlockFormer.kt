package com.suslovila.sus_multi_blocked.client.gui

import com.suslovila.sus_multi_blocked.SusMultiBlocked
import com.suslovila.sus_multi_blocked.common.item.Modifier
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getBlockInfo
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getFileName
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getMode
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getModifiers
import com.suslovila.sus_multi_blocked.common.sync.PacketBlockModifiers
import com.suslovila.sus_multi_blocked.common.sync.PacketHandler
import com.suslovila.sus_multi_blocked.common.sync.PacketSetGlobalModifiers
import com.suslovila.sus_multi_blocked.utils.SerialiseType
import com.suslovila.sus_multi_blocked.utils.SusNBTHelper.getOrCreateTag
import com.suslovila.sus_multi_blocked.utils.Position
import com.suslovila.sus_multi_blocked.utils.collection.nextCycled
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

class GuiMultiBlockFormer(invPlayer: InventoryPlayer, multiBlockFormer: ItemStack, pos: Position) : GuiScreen() {

    companion object {
        const val enterButtonKeyboardId = 28
        private val texture = ResourceLocation(SusMultiBlocked.MOD_ID, "textures/gui/PlayerDetector2.png")
        private val modifiersMaxAmount = 10
    }

    val pos: Position
    lateinit var modifierNameTextFields: Array<GuiTextField>
    lateinit var valueTextFields: Array<GuiTextField>
    lateinit var fileNameField: GuiTextField

    var player: EntityPlayer
    private val multiBlockFormer: ItemStack

    private val isZoneSelector: Boolean
        get() {
            return multiBlockFormer.getMode() == MultiBlockWrapper.MODE.ZONE_SELECTOR
        }

    private var currentModifier: Modifier? = null
    private var currentField: GuiTextField? = null

    private val modifierTypes: Array<SerialiseType> = Array(modifiersMaxAmount) { SerialiseType.INTEGER }

    /** The X size of the inventory window in pixels.  */
    protected var xTextureSize = 511

    /** The Y size of the inventory window in pixels.  */
    protected var yTextureSize = 367

    protected var guiLeft: Int = 0
    protected var guiTop: Int = 0


    init {
        this.multiBlockFormer = multiBlockFormer
        player = invPlayer.player
        this.pos = pos
    }

    override fun initGui() {
        super.initGui()
        val globalModifiers = multiBlockFormer.getOrCreateTag().getModifiers()
        val singleBlockModifiers = multiBlockFormer.getBlockInfo()[pos] ?: ArrayList()
        xTextureSize = 256
        yTextureSize = 198
        guiLeft = (width - this.xTextureSize) / 2
        guiTop = (height - this.yTextureSize) / 2
        buttonList.clear()

        //setting already existing types
        for (i in 0 until globalModifiers.size) {
            modifierTypes[i] = globalModifiers[i].type
        }

        for (i in modifierTypes.indices) {
            buttonList.add(
                GuiButton(
                    i,
                    guiLeft + 105,
                    guiTop + i * 16 + 30,
                    40,
                    12,
                    modifierTypes[i].toString()
                )
            )
        }

        modifierNameTextFields = Array(modifiersMaxAmount) { id ->
            val field = GuiTextField(fontRendererObj, guiLeft, guiTop + id * 16 + 30, 100, 12)
            field.text = if (id < globalModifiers.size) globalModifiers[id].name else ""
            setDefaultTextSettings(field)
            field
        }

        valueTextFields = Array(modifiersMaxAmount) { id ->
            //if block already has its value for modifier - we will set it, else - use the global value
            val field = GuiTextField(fontRendererObj, guiLeft + 150, guiTop + id * 16 + 30, 100, 12)
            val doesGlobalModifierExist = id < globalModifiers.size
            if (doesGlobalModifierExist) {
                val globalModifier = globalModifiers[id]
                val blockWithItsOwnValueForModifier =
                    (singleBlockModifiers.firstOrNull { it.name == globalModifier.name })
                field.text =
                    blockWithItsOwnValueForModifier?.value?.toString() ?: globalModifier.value.toString()
            } else {
                field.text = ""
            }
            setDefaultTextSettings(field)
            field
        }
        fileNameField = GuiTextField(fontRendererObj, guiLeft + 30, guiTop + 5, 200, 12)
        fileNameField.text = multiBlockFormer.getFileName()
        setDefaultTextSettings(fileNameField)
    }

    override fun drawScreen(x: Int, y: Int, p_73863_3_: Float) {
        super.drawScreen(x, y, p_73863_3_)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(texture)
        val prevZlevel = this.zLevel
        this.zLevel = -100f
        this.zLevel = prevZlevel
        drawGuiText(x, y)
    }

    override fun drawBackground(p_146278_1_: Int) {

    }

    private fun setDefaultTextSettings(textField: GuiTextField) {
        textField.setTextColor(-1)
        textField.setDisabledTextColour(-1)
        textField.enableBackgroundDrawing = true
        textField.maxStringLength = 40
    }

    override fun actionPerformed(button: GuiButton) {
        if (isZoneSelector) {
            val previousType = modifierTypes[button.id]
            val nextType = SerialiseType.values().toList().nextCycled(previousType) ?: SerialiseType.INTEGER
            modifierTypes[button.id] = nextType
            button.displayString = nextType.toString()
        }
    }

    override fun keyTyped(par1: Char, keyId: Int) {
        if (isZoneSelector) {
            for (indexedTextField in modifierNameTextFields.withIndex()) {

                if (indexedTextField.value.textboxKeyTyped(par1, keyId)) return
                if (keyId == enterButtonKeyboardId) {
                    if (indexedTextField.value.isFocused) {
                        indexedTextField.value.isFocused = false
                        currentField = null
                        return
                    }
                }
            }
        }

        for (textField in valueTextFields) {
            if (textField.textboxKeyTyped(par1, keyId)) return
            if (keyId == enterButtonKeyboardId) {
                if (textField.isFocused) {
                    textField.isFocused = false
                    currentField = null
                    return
                }
            }
        }
        if (fileNameField.textboxKeyTyped(par1, keyId)) return
        if (keyId == enterButtonKeyboardId) {
            if (fileNameField.isFocused) {
                fileNameField.isFocused = false
                currentField = null
                return
            }
        }
        super.keyTyped(par1, keyId)

    }

    override fun updateScreen() {
        super.updateScreen()
    }

    override fun mouseClicked(x: Int, y: Int, par3: Int) {
        super.mouseClicked(x, y, par3)
        selectName(x - guiLeft, y - guiTop)

    }

    private fun drawGuiText(rawX: Int, rawY: Int) {
        fontRendererObj.drawString(multiBlockFormer.getMode().toString(), guiLeft + 80, guiTop - 10, 0xFFFFFF)
        modifierNameTextFields.forEach { it.drawTextBox() }
        valueTextFields.forEach { it.drawTextBox() }
        if (isZoneSelector) fileNameField.drawTextBox()
        val prevLevel = this.zLevel
        this.zLevel = -99f
        val names = hashMapOf<String, Int>()
        for (nameField in modifierNameTextFields) names[nameField.text] = (names[nameField.text] ?: 0) + 1
        for (index in 0 until modifiersMaxAmount) {
            val nameField = modifierNameTextFields[index]
            val valueField = valueTextFields[index]
            val type = modifierTypes[index]
            if (!nameField.isEmpty() && (names[nameField.text] ?: 0) > 1) {
                fontRendererObj.drawString(
                    "WARNING: DOUBLED MODIFIER NAME",
                    valueField.xPosition + valueField.width + 10,
                    valueField.yPosition,
                    0x980000
                )
                continue
            }
            if (nameField.isEmpty() && valueField.isEmpty()) {
                continue
            }
            if (!nameField.isEmpty() && !valueField.isEmpty()) {
                if (type.cast(valueTextFields[index].text) == null) {
                    fontRendererObj.drawString(
                        "Wrong value format",
                        valueField.xPosition + valueField.width + 10,
                        valueField.yPosition,
                        0x980000
                    )
                }
                continue
            }
            fontRendererObj.drawString(
                "Modifier not finalised",
                valueField.xPosition + valueField.width + 10,
                valueField.yPosition,
                0x980000
            )
            continue

        }

        Minecraft.getMinecraft().textureManager.bindTexture(texture)

        this.zLevel = prevLevel

    }


    fun selectName(xClick: Int, yClick: Int) {
        if (currentField != null) return
        if (isZoneSelector) {
            for (textField in modifierNameTextFields) {
                if (isMouseOnField(textField, xClick, yClick)) {
                    textField.isFocused = true
                    currentField = textField
                    return
                }
            }
        }

        for (id in valueTextFields.indices) {
            val field = valueTextFields[id]
            if (isMouseOnField(field, xClick, yClick)) {
                if (modifierTypes[id] == SerialiseType.BOOLEAN) {
                    field.text = (!(valueTextFields[id].text.toBoolean())).toString()
                    return
                }
                field.isFocused = true
                currentField = field
                return

            }

        }
        if (isMouseOnField(fileNameField, xClick, yClick)) {
            fileNameField.isFocused = true
            currentField = fileNameField
            return

        }
    }

    fun isModifierFinalised(id: Int): Boolean {
        val value = valueTextFields[id].text
        return modifierNameTextFields[id].text != "" &&
                value != "" &&
                modifierTypes[id].cast(value) != null

    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        val actualModifiers = ArrayList<Modifier>()
        val amountOfNames = getAmountOfNames()
        for (i in 0 until modifiersMaxAmount) {
            if (isModifierFinalised(i)) {
                val name = modifierNameTextFields[i].text
                val isExactlyOneWithSuchName = amountOfNames[name] == 1
                if (!isExactlyOneWithSuchName) continue
                actualModifiers.add(
                    Modifier(
                        name = modifierNameTextFields[i].text,
                        type = modifierTypes[i],
                        value = modifierTypes[i].cast(valueTextFields[i].text) ?: throw Exception("error casting")
                    )
                )
            }
        }
        if (isZoneSelector) {
            PacketHandler.INSTANCE.sendToServer(
                PacketSetGlobalModifiers(
                    actualModifiers,
                    fileNameField.text
                )
            )
        } else {
            PacketHandler.INSTANCE.sendToServer(
                PacketBlockModifiers(
                    actualModifiers,
                    pos
                )
            )
        }
    }

    fun isMouseOnField(field: GuiTextField, xClick: Int, yClick: Int): Boolean =
        ((xClick + guiLeft) < (field.xPosition + field.width) && (xClick + guiLeft) > field.xPosition)
                && ((yClick + guiTop) < (field.yPosition + field.height) && (yClick + guiTop) > field.yPosition)


    fun getAmountOfNames(): HashMap<String, Int> {
        val names = hashMapOf<String, Int>()
        for (nameField in modifierNameTextFields) names[nameField.text] = (names[nameField.text] ?: 0) + 1
        return names;
    }
}

fun GuiTextField.isEmpty() = this.text == ""