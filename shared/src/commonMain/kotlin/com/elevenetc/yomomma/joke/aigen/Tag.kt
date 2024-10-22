package com.elevenetc.yomomma.joke.aigen

data class Tag(val title: String) {
    var selected: Boolean = false

    constructor(title: String, selected: Boolean) : this(title) {
        this.selected = selected
    }
}

class TagsSet {
    internal constructor(
        selectionType: TagSelectionType,
        initSet: Set<Tag> = emptySet()
    ) {
        this.tags = initSet.associateBy { it.title }.toMutableMap()
        this.selectionType = selectionType
    }

    private val tags: MutableMap<String, Tag>
    val selectionType: TagSelectionType

    val allTags: List<Tag>
        get() {
            return tags.values.toList()
        }

    val allTagsTitles: List<String>
        get() {
            return allTags.map { it.title }
        }

    fun selectedTags(): List<Tag> {
        return tags.filter { it.value.selected }.values.toList()
    }

    fun setSelection(tag: Tag, selected: Boolean) {
        if(selectionType == TagSelectionType.SINGLE) tags.values.forEach { it.selected = false }
        tags[tag.title] = Tag(tag.title, selected)
    }

    fun tagIdToTag(id: String): Tag? {
        return tags.values.firstOrNull { it.title == id }
    }
}

enum class TagSelectionType {
    SINGLE, MULTIPLE
}

val TAGS = TagsSet(
    TagSelectionType.SINGLE,
    setOf(
        "fat",
        "old",
        "old fashioned",
        "huge",
        "substantial",
        "morally flexible",
        "opportunistic",
        "machiavellian",
        "utilitarian",
        "realpolitik practitioner",
        "pragmatic Idealist",
        "amoral",
        "expedient",
        "ethical chameleon",
        "situational moralist",
        "non-dogmatic moralist",
        "philosophical nomad",
        "principled negotiator"
    ).map { Tag(it) }.sortedBy { it.title }.toSet()
)