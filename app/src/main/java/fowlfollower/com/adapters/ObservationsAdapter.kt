package fowlfollower.com.adapters


import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fowlfollower.com.ExpandedCardView
import fowlfollower.com.Observations
import fowlfollower.com.R
import java.io.File


class ObservationsAdapter(private val observations: List<Observations.Observation>) :
    RecyclerView.Adapter<ObservationsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define the views in your observation item layout
        val titleView: TextView = itemView.findViewById(R.id.textViewTitle)
        val descriptionView: TextView = itemView.findViewById(R.id.textViewDescription)
        val dateView: TextView = itemView.findViewById(R.id.textViewDate)
        val locationView: TextView = itemView.findViewById(R.id.textViewLocation)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewObservation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_add, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val observation = observations[position]

        // Bind the observation data to the views in the item layout
        holder.titleView.text = observation.title
        holder.descriptionView.text = observation.description
        holder.dateView.text = observation.date
        holder.locationView.text = observation.location
       // Picasso.get().load(observation.imageUrl).into(holder.imageView)
        val imageUri = Uri.fromFile(File(observation.imageUrl))
        Picasso.get().load(imageUri).into(holder.imageView)

        holder.itemView.setOnClickListener {
            // Handle item click
            val intent = Intent(holder.itemView.context, ExpandedCardView::class.java)
            intent.putExtra("title", observation.title)
            intent.putExtra("description", observation.description)
            intent.putExtra("date", observation.date)
            intent.putExtra("location", observation.location)
            intent.putExtra("imageUrl", observation.imageUrl)
            //intent.putExtra("audioFileUri", observation.audioFileUri)
            holder.itemView.context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return observations.size
    }
}
