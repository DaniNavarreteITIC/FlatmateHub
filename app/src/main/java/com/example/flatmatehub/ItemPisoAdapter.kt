package com.example.flatmatehub
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
/**
 * Adaptador personalitzat per al [RecyclerView] de la pantalla principal de Flatmate Hub.
 * Vincula la llista de tasques i despeses compartides del pis amb les targetes de la interfície
 * i delega les interaccions de l'usuari sobre cada fila.
 *
 * @property listaVisible Llista mutable d'elements ([ItemPiso]) filtrats que es mostren actualment.
 * @property onEditar Funció callback executada en prémer breument sobre un element per editar-lo.
 * @property onEliminar Funció callback executada en prémer el botó de paperera per esborrar un element.
 * @property onToggleCompletado Funció callback executada en mantenir premut un element per alternar el seu estat.
 */
class ItemPisoAdapter(
    private var listaVisible: MutableList<ItemPiso>,
    private val onEditar: (ItemPiso) -> Unit,
    private val onEliminar: (ItemPiso) -> Unit,
    private val onToggleCompletado: (ItemPiso) -> Unit
) : RecyclerView.Adapter<ItemPisoAdapter.ItemViewHolder>() {
    /**
     * Contenidor de vistes ([RecyclerView.ViewHolder]) que emmagatzema les referències als
     * components gràfics de cada fila (títol, detall, icona de tipus i botó d'eliminar).
     *
     * @param view Vista arrel inflada a partir del layout `item_flatmate_row.xml`.
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloItem)
        val tvDetalle: TextView = view.findViewById(R.id.tvSubtituloItem)
        val ivIcono: ImageView = view.findViewById(R.id.ivIconoTipo)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminarItem)
        /**
         * Assigna les dades d'un [ItemPiso] als elements visuals de la targeta,
         * aplica el ratllat si la tasca o despesa està completada i configura els esdeveniments tàctils.
         *
         * @param item Objecte [ItemPiso] les dades del qual es representaran a la fila.
         */
        fun bind(item: ItemPiso) {
            tvTitulo.text = item.titulo
            tvTitulo.paintFlags = if (item.completado) {
                tvTitulo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tvTitulo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            val textoImporte = if (item.tipo == TipoItem.GASTO && item.importe != null) {
                " · ${String.format("%.2f", item.importe)} €"
            } else ""
            tvDetalle.text = "Asignado: ${item.asignadoA} · ${item.fechaLimite}$textoImporte"

            ivIcono.setImageResource(
                if (item.tipo == TipoItem.TAREA) R.drawable.ic_tarea else R.drawable.ic_euro
            )

            // Pulsación corta: editar elemento
            itemView.setOnClickListener { onEditar(item) }

            // Pulsación larga: marcar como completado/pagado
            itemView.setOnLongClickListener {
                onToggleCompletado(item)
                true
            }

            // Botón papelera: eliminar elemento
            btnEliminar.setOnClickListener { onEliminar(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flatmate_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listaVisible[position])
    }

    override fun getItemCount(): Int = listaVisible.size
    /**
     * Actualitza les dades mostrades al llistat després d'aplicar un filtre o modificar elements
     * i notifica al [RecyclerView] per refrescar la vista.
     *
     * @param nuevaLista Nova llista d'[ItemPiso] que s'ha de renderitzar.
     */
    fun refrescarDatos(nuevaLista: MutableList<ItemPiso>) {
        listaVisible = nuevaLista
        notifyDataSetChanged()
    }
}