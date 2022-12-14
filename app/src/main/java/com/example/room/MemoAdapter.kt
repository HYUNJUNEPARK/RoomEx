package com.example.room

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.room.databinding.ItemRecyclerBinding
import com.example.room.db.Memo
import com.example.room.db.MemoDao
import com.example.room.vm.MemoViewModel
import java.text.SimpleDateFormat
import java.util.*

class MemoAdapter(val context: Context): ListAdapter<Memo, MemoAdapter.ViewHolder>(diffUtil) {
    lateinit var memoDao: MemoDao
    private val viewModel: MemoViewModel by (context as ComponentActivity).viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.recyclerViewItem = this
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    inner class ViewHolder(private val binding: ItemRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
        private lateinit var memo: Memo
        private var idx: Int? = null

        init {
            binding.deleteButton.setOnClickListener {
                viewModel.deleteData(context, memo)
            }

            binding.modifyButton.setOnClickListener {
                if(binding.modifyButton.text == "수정") {
                    binding.textEditor.visibility = View.VISIBLE
                    binding.modifyButton.text = "저장"
                    binding.modifyButton.setTextColor(Color.RED)
                    binding.textEditor.setText(memo.content)
                } else {
                    binding.textEditor.visibility = View.INVISIBLE
                    binding.modifyButton.text = "수정"
                    binding.modifyButton.setTextColor(Color.WHITE)

                    viewModel.updateData(
                        context = context,
                        newMemo = Memo(
                            id = memo.id,
                            content = binding.textEditor.text.toString(),
                            datetime = System.currentTimeMillis()
                        ),
                        idx = idx!!
                    )
                }
            }
        }

        fun bind(memo: Memo, idx: Int) {
            this.memo = memo
            this.idx = idx
            binding.textIdx.text = "${memo.id}"
            binding.textContent.text = memo.content
            val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.KOREA)
            binding.textDatetime.text = sdf.format(memo.datetime)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(currentList[position], position)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Memo>() {
            override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
                return oldItem == newItem
            }
        }
    }
}