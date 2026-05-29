package com.f0x1d.logfox.feature.filters.presentation.edit

import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.logging.api.model.LogLevel

internal data class EditFilterState(
    val filter: UserFilter?,
    val name: String?,
    val including: Boolean,
    val enabled: Boolean,
    val enabledLogLevels: List<Boolean>,
    val uid: String?,
    val pid: String?,
    val tid: String?,
    val packageName: String?,
    val tag: String?,
    val content: String?,
    // Baseline captured on initial load (or new-filter creation) to detect unsaved edits.
    val initial: EditFilterSnapshot,
) {
    val isDirty: Boolean
        get() = EditFilterSnapshot.from(this) != initial
}

// Captures the current fields as the baseline so a freshly loaded existing filter starts not-dirty.
internal fun EditFilterState.withInitialBaseline() = copy(initial = EditFilterSnapshot.from(this))

// Comparable projection of the editable form fields. Blank text is normalized to null so
// whitespace-only edits don't count as changes, matching how the repository persists fields.
internal data class EditFilterSnapshot(
    val including: Boolean,
    val enabled: Boolean,
    val enabledLogLevels: List<Boolean>,
    val uid: String?,
    val pid: String?,
    val tid: String?,
    val packageName: String?,
    val tag: String?,
    val content: String?,
) {
    companion object {
        // Baseline for a brand-new filter: everything empty/default. A blank new filter matches it
        // (not dirty); a prefilled new filter differs from it (dirty).
        val empty = EditFilterSnapshot(
            including = true,
            enabled = true,
            enabledLogLevels = List(LogLevel.entries.size) { false },
            uid = null,
            pid = null,
            tid = null,
            packageName = null,
            tag = null,
            content = null,
        )

        fun from(state: EditFilterState) = EditFilterSnapshot(
            including = state.including,
            enabled = state.enabled,
            enabledLogLevels = state.enabledLogLevels,
            uid = state.uid?.nullIfBlank(),
            pid = state.pid?.nullIfBlank(),
            tid = state.tid?.nullIfBlank(),
            packageName = state.packageName?.nullIfBlank(),
            tag = state.tag?.nullIfBlank(),
            content = state.content?.nullIfBlank(),
        )

        private fun String.nullIfBlank() = takeIf { it.isNotBlank() }
    }
}
