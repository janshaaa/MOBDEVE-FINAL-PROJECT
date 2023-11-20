import com.mobdeve.s12.aiwear.models.ClothesItem
import java.util.Date

class OutfitModel (
    val user_uuid: String,
    var date: Date,
    var clothes: List<ClothesItem>
) {
    
}
