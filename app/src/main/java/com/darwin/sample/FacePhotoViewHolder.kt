package com.darwin.sample

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.darwin.viola.still.model.FacePortrait

/**
 * The class FacePhotoViewHolder
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 12 Jul 2020
 */
class FacePhotoViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
    private var view: View = v
    private var photo: FacePortrait? = null

    fun bind(face: FacePortrait) {
        photo = face
        view.findViewById<ImageView>(R.id.iv_face).setImageBitmap(face.face)
        Log.e("FACE",face.toString())
    }

    init {
        v.setOnClickListener(this)
    }

    override fun onClick(v: View) {
    }
}