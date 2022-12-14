package com.example.room.vm

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.room.db.AppDatabase
import com.example.room.db.Memo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoViewModel : ViewModel() {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val _memoList = MutableLiveData<List<Memo>>()
    val memoList: LiveData<List<Memo>>
        get() = _memoList

    fun getDbData(context: Context) {
        try {
            ioScope.launch {
                val db = AppDatabase.getInstance(context)
                db!!.memoDao().getAll().let { dbDataList ->
                    mainScope.launch {
                        _memoList.value = dbDataList
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun insertData(context: Context, memo: Memo) {
        try {
            ioScope.launch {
                val db = AppDatabase.getInstance(context)
                db!!.memoDao().insert(memo)
            }
            val newMemoList = _memoList.value!!.toMutableList().apply {
                this.add(memo)
            }
            _memoList.value = newMemoList
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun updateData(context: Context, newMemo: Memo, idx: Int) {
        try {
            ioScope.launch {
                val db = AppDatabase.getInstance(context)
                db!!.memoDao().update(newMemo)
            }
            val newMemoList = _memoList.value!!.toMutableList().apply {
                this.removeAt(idx)
                this.add(idx, newMemo)
            }
            _memoList.value = newMemoList
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteData(context: Context, memo: Memo) {
        try {
            ioScope.launch {
                val db = AppDatabase.getInstance(context)
                db!!.memoDao().delete(memo)
            }
            val newMemoList = _memoList.value!!.toMutableList().apply {
                this.remove(memo)
            }
            _memoList.value = newMemoList
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}