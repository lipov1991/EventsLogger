package pl.lipov.eventslogger.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.lipov.eventslogger.R

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private val compositeDisposable = CompositeDisposable()
    private var logAdapter: LogAdapter? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logAdapter = LogAdapter(emptyList())
        logs_list.adapter = logAdapter
        viewModel.onCreate(this)
        info_text.setOnTouchListener(viewModel.getOnTouchListener())
        viewModel.registerSensorEventListener()
        observeLiveDataEvents()
        observeRxEvents()
    }

    private fun observeLiveDataEvents() {
        viewModel.run {
            onNoAccelerometerDetected.observe(::getLifecycle) {
                info_text.text = getString(R.string.no_accelerometer_detected)
            }
            onGestureEventReceived.observe(::getLifecycle) {
                logNewEventIntoFile(it)
                info_text.text = it
                setFoldActionIcon(R.drawable.ic_more)
            }
            onSensorEventReceived.observe(::getLifecycle) {
                info_text.text = it
                viewModel.logNewEventIntoFile(it)
            }
            onFoldOut.observe(::getLifecycle) {
                setFoldActionIcon(R.drawable.ic_less)
                view_switcher.showNext()
            }
            onFoldIn.observe(::getLifecycle) {
                setFoldActionIcon(R.drawable.ic_more)
                view_switcher.showNext()
            }
        }
    }

    private fun observeRxEvents() {
        compositeDisposable.addAll(
            viewModel.eventsLogsFileCreated.getErrorStream().subscribe {
                showToast(it.localizedMessage ?: getString(R.string.logs_file_cannot_be_created))
            },
            viewModel.eventsLogsReceived.getSuccessStream().subscribe {
                logAdapter?.saveLogs(it)
            },
            viewModel.eventsLogsReceived.getErrorStream().subscribe {
                showToast(it.localizedMessage ?: getString(R.string.logs_cannot_be_read))
            },
            viewModel.eventsLogsSaved.getErrorStream().subscribe {
                showToast(it.localizedMessage ?: getString(R.string.logs_cannot_be_saved))
            },
            viewModel.eventsLogsUploaded.getSuccessStream().subscribe {
                showToast(getString(R.string.logs_uploaded_successfully))
            },
            viewModel.eventsLogsUploaded.getErrorStream().subscribe {
                showToast(it.localizedMessage ?: getString(R.string.logs_cannot_be_uploaded))
            },
            viewModel.eventsLogsCleared.getSuccessStream().subscribe {
                info_text.text = ""
                showToast(getString(R.string.logs_cleared_successfully))
            },
            viewModel.eventsLogsCleared.getErrorStream().subscribe {
                info_text.text = ""
                showToast(it.localizedMessage ?: getString(R.string.logs_cannot_be_cleared))
            }
        )
    }

    private fun setFoldActionIcon(
        @DrawableRes icon: Int
    ) {
        viewModel.menu?.findItem(R.id.action_fold)?.icon = ContextCompat.getDrawable(this, icon)
    }

    private fun showToast(
        message: String
    ) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onCreateOptionsMenu(
        menu: Menu
    ): Boolean {
        viewModel.menu = menu
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean = when (item.itemId) {
        R.id.action_upload -> {
            viewModel.uploadLogs()
            true
        }
        R.id.action_fold -> {
            viewModel.toggleFoldMode()
            true
        }
        R.id.action_delete -> {
            viewModel.clearLogs()
            setFoldActionIcon(R.drawable.ic_more)
            viewModel.foldIn = true
            view_switcher.displayedChild = 0
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {
        viewModel.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        viewModel.onDestroy()
        super.onDestroy()
    }
}
