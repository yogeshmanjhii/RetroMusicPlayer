package code.name.monkey.retromusic.adapter.album

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.retromusic.glide.AlbumGlideRequest
import code.name.monkey.retromusic.glide.RetroMusicColoredTarget
import code.name.monkey.retromusic.helper.HorizontalAdapterHelper
import code.name.monkey.retromusic.interfaces.CabHolder
import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.util.MusicUtil
import com.bumptech.glide.Glide
import java.util.ArrayList

class HorizontalAlbumAdapter(
    activity: AppCompatActivity,
    dataSet: ArrayList<Album>,
    cabHolder: CabHolder?
) : AlbumAdapter(
    activity, dataSet, HorizontalAdapterHelper.LAYOUT_RES, cabHolder
) {

    override fun createViewHolder(view: View, viewType: Int): ViewHolder {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        HorizontalAdapterHelper.applyMarginToLayoutParams(activity, params, viewType)
        return ViewHolder(view)
    }

    override fun setColors(color: Int, holder: ViewHolder) {
        holder.title?.setTextColor(ATHUtil.resolveColor(activity, android.R.attr.textColorPrimary))
        holder.text?.setTextColor(ATHUtil.resolveColor(activity, android.R.attr.textColorSecondary))
    }

    override fun loadAlbumCover(album: Album, holder: ViewHolder) {
        if (holder.image == null) return
        AlbumGlideRequest.Builder(Glide.with(activity), album.id)
            .generatePalette(activity)
            .build()
            .dontAnimate()
            .dontTransform()
            .into(object : RetroMusicColoredTarget(holder.image!!) {
                override fun onColorReady(color: Int) {
                    //setColors(color, holder)
                }
            })
    }

    override fun getAlbumText(album: Album): String? {
        return MusicUtil.getYearString(album.year)
    }

    override fun getItemViewType(position: Int): Int {
        return HorizontalAdapterHelper.getItemViewtype(position, itemCount)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    companion object {
        val TAG: String = AlbumAdapter::class.java.simpleName
    }
}
