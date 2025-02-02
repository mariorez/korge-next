import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.image
import com.soywiz.korge.view.renderToBitmap
import com.soywiz.korge.view.scale
import com.soywiz.korge.view.size
import com.soywiz.korge.view.unsafeRenderToBitmapSync
import com.soywiz.korge.view.xy
import com.soywiz.korim.bitmap.Bitmaps
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.Size

suspend fun Stage.mainColorPicker() {
    image(resourcesVfs["korge.png"].readBitmap())

    val magnifier = image(Bitmaps.white).xy(720, 200).scale(10, 10).apply { smoothing = false }

    addUpdater {
        val bmp = stage!!.unsafeRenderToBitmapSync(views!!.renderContext, Rectangle(mouseX - 5.0, mouseY - 5.0, 10.0, 10.0), views!!.globalToWindowScaleAvg)
        magnifier.bitmap = bmp.slice()
    }
}
