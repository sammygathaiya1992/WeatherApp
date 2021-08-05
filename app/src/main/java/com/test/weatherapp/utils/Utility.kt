package com.test.weatherapp.utils

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object Utility {

    private var progressBar: ProgressBar? = null
    private val stringFormat = DecimalFormat("###,###")


    // show progressbar
    fun showProgressBar(context: Context, activity: AppCompatActivity) {
        try {
            val layout = (context as? Activity)?.findViewById<View>(R.id.content)?.rootView as? ViewGroup

            progressBar = ProgressBar(context, null, R.attr.progressBarStyleLarge)
            progressBar?.let { it1 ->
                it1.isIndeterminate = true

                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )

                val rl = RelativeLayout(context)

                rl.gravity = Gravity.CENTER
                rl.addView(it1)

                layout?.addView(rl, params)

                it1.visibility = View.VISIBLE
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // hide progressbar
    fun hideProgressBar(activity: AppCompatActivity) {
        try {
            progressBar?.let {
                it.visibility = View.GONE
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showCustomErrorDialog(context: Context, message: String?, title: String?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.test.weatherapp.R.layout.dialog_error)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(com.test.weatherapp.R.id.title) as TextView).text= title
        (dialog.findViewById<View>(com.test.weatherapp.R.id.content) as TextView).text= message
        (dialog.findViewById<View>(com.test.weatherapp.R.id.bt_close) as AppCompatButton).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    fun getDayName(date: String): String{
        val inFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = inFormat.parse(date)
        val outFormat = SimpleDateFormat("EEEE")
        return outFormat.format(date)
    }

    fun isNetWorkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun convertToString(temp: Double): String{
        return stringFormat.format(temp)
    }


    fun showSuccessToast(context: Context,message: String?) {
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        val layoutInflater:LayoutInflater = LayoutInflater.from(context)

        //inflate view
        val custom_view: View =
            layoutInflater.inflate(com.test.weatherapp.R.layout.custom_toast_view, null)
        (custom_view.findViewById<View>(com.test.weatherapp.R.id.message) as TextView).text = message
        (custom_view.findViewById<View>(com.test.weatherapp.R.id.icon) as ImageView).setImageResource(
            com.test.weatherapp.R.drawable.ic_done
        )
        (custom_view.findViewById<View>(com.test.weatherapp.R.id.parent_view) as CardView).setCardBackgroundColor(
            context.resources.getColor(com.test.weatherapp.R.color.colorPrimary)
        )
        toast.view = custom_view
        toast.show()
    }


    fun showErrorToast(context: Context,message: String?) {
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        val layoutInflater:LayoutInflater = LayoutInflater.from(context)

        //inflate view
        val custom_view: View =
            layoutInflater.inflate(com.test.weatherapp.R.layout.custom_toast_view, null)
        (custom_view.findViewById<View>(com.test.weatherapp.R.id.message) as TextView).text = message
        (custom_view.findViewById<View>(com.test.weatherapp.R.id.icon) as ImageView).setImageResource(
            com.test.weatherapp.R.drawable.ic_error
        )
        (custom_view.findViewById<View>(com.test.weatherapp.R.id.parent_view) as CardView).setCardBackgroundColor(
            context.resources.getColor(com.test.weatherapp.R.color.red_300)
        )
        toast.view = custom_view
        toast.show()
    }

}