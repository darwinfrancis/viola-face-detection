package com.darwin.sample

import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
        face.ageRange?.let {
            val ageRange = "Age: $it"
            view.findViewById<TextView>(R.id.tvAge).text = ageRange
        }
    }

    init {
        v.setOnClickListener(this)
    }

    override fun onClick(v: View) {
    }
}