package com.darwin.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darwin.viola.still.model.FacePortrait

/**
 * The class FacePhotoAdapter
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 12 Jul 2020
 */
class FacePhotoAdapter : RecyclerView.Adapter<FacePhotoViewHolder>() {

    private var faceList: List<FacePortrait> = ArrayList()

    fun bindData(faceList: List<FacePortrait>) {
        this.faceList = faceList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacePhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_image, parent, false)
        return FacePhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (faceList.isEmpty()) {
            0
        } else {
            faceList.size
        }
    }

    override fun onBindViewHolder(holder: FacePhotoViewHolder, position: Int) {
        holder.bind(faceList[position])
    }
}