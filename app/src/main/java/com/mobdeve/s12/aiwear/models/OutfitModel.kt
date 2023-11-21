import com.mobdeve.s12.aiwear.models.ClothesItem
import java.util.Date

class OutfitModel(
    val outfit_id: Long = -1L,
    val user_uuid: String,
    var date: Date,
    var clothes: List<ClothesItem>? = null
) {
    var delimitted_clothes = ""

    fun formatClothesId(): String {
        var delimitted_clothes = ""
        if(clothes != null){
            for (c in clothes!!) {
                delimitted_clothes = delimitted_clothes + c.clothes_id + "|"
            }
        }
        return delimitted_clothes
    }

    fun parseClothesId() {
        var ids = this.delimitted_clothes.split("|")
        // query db with IDs
        // store into this.clothes
    }
    
}
