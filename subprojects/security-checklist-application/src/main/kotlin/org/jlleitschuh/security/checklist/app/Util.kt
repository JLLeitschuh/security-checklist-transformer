package org.jlleitschuh.security.checklist.app

fun <T> Collection<T>.mapIf(condition: Boolean, transform: (T) -> T): List<T> =
    map { if(condition) transform(it) else it }
