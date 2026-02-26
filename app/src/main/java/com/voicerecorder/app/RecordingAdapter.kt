package com.voicerecorder.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordingAdapter(
    private val recordings: List<Recording>,
    private val onPlay: (Recording) -> Unit,
    private val onDelete: (Recording) -> Unit,
    private val onTranscribe: (Recording) -> Unit,
    private val onSummarize: (Recording) -> Unit
) : RecyclerView.Adapter<RecordingAdapter.ViewHolder>() {

    private var playingId: String? = null

    fun setPlaying(id: String?) {
        playingId = id
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        val tvTranscriptPreview: TextView = view.findViewById(R.id.tvTranscriptPreview)
        val btnPlay: LinearLayout = view.findViewById(R.id.btnPlay)
        val ivPlayIcon: ImageView = view.findViewById(R.id.ivPlayIcon)
        val tvPlayText: TextView = view.findViewById(R.id.tvPlayText)
        val btnTranscribe: LinearLayout = view.findViewById(R.id.btnTranscribe)
        val btnSummarize: LinearLayout = view.findViewById(R.id.btnSummarize)
        val btnDelete: LinearLayout = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recording, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recording = recordings[position]
        holder.tvName.text = recording.name
        holder.tvDate.text = recording.date
        holder.tvDuration.text = recording.duration

        // 显示转写预览
        if (!recording.transcript.isNullOrEmpty()) {
            holder.tvTranscriptPreview.visibility = View.VISIBLE
            holder.tvTranscriptPreview.text = "📄 ${recording.transcript}"
        } else {
            holder.tvTranscriptPreview.visibility = View.GONE
        }

        // 播放状态
        val isPlaying = recording.id == playingId
        holder.ivPlayIcon.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        holder.tvPlayText.text = if (isPlaying) " 暂停" else " 播放"

        holder.btnPlay.setOnClickListener { onPlay(recording) }
        holder.btnTranscribe.setOnClickListener { onTranscribe(recording) }
        holder.btnSummarize.setOnClickListener { onSummarize(recording) }
        holder.btnDelete.setOnClickListener { onDelete(recording) }
    }

    override fun getItemCount() = recordings.size
}
