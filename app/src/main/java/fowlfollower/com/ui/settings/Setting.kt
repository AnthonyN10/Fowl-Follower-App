package fowlfollower.com.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import fowlfollower.com.databinding.FragmentGalleryBinding

import fowlfollower.com.databinding.FragmentSettingBinding
import fowlfollower.com.ui.gallery.GalleryViewModel

class Setting : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textView14
        settingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
   /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        // TODO: Use the ViewModel
    }*/

}
/*
class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val seekBar = findViewById<SeekBar>(R.id.seekBar2)
        val textView = findViewById<TextView>(R.id.textView)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Update the TextView with the current progress value
                textView.text = "$progress" + "km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something when the user starts sliding the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something when the user stops sliding the SeekBar
            }
        })


    }
}*/
