package likelion.project.agijagi.search

data class SearchResultModel(
    val prodId: String,
    val prodIsCustom: Boolean?,
    val prodImage: String,
    val prodBrand: String,
    val prodName: String,
    val prodPrice: String
)
