/*
 * Copyright (C) 2023-2024 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.selfie

/** A camera transforms a subject of a specific type into a [Snapshot]. */
fun interface Camera<Subject> {
  fun snapshot(subject: Subject): Snapshot
  fun withLens(lens: Lens): Camera<Subject> {
    val parent = this
    return object : Camera<Subject> {
      override fun snapshot(subject: Subject): Snapshot = lens.transform(parent.snapshot(subject))
    }
  }
}

/**
 * A lens transforms a single [Snapshot] into a new [Snapshot], transforming / creating / removing
 * [SnapshotValue]s along the way.
 */
fun interface Lens {
  fun transform(snapshot: Snapshot): Snapshot
}

/** A function which transforms a string into either another string or null. */
fun interface StringOptionalFunction {
  fun apply(value: String): String?
  fun then(next: StringOptionalFunction): StringOptionalFunction = StringOptionalFunction { outer ->
    apply(outer)?.let { inner -> next.apply(inner) }
  }
}

/** A lens which makes it easy to pipe data from one facet to another within a snapshot. */
open class CompoundLens : Lens {
  private val lenses = mutableListOf<Lens>()
  fun add(lens: Lens) {
    lenses.add(lens)
  }
  fun replaceAll(toReplace: Regex, replacement: String) = mutateStrings {
    it.replace(toReplace, replacement)
  }
  fun mutateStrings(perString: StringOptionalFunction) {
    add { snapshot ->
      Snapshot.ofEntries(
          snapshot.allEntries().mapNotNull { e ->
            if (e.value.isBinary) e
            else perString.apply(e.value.valueString())?.let { entry(e.key, SnapshotValue.of(it)) }
          })
    }
  }
  fun setFacetFrom(target: String, source: String, function: StringOptionalFunction) {
    add { snapshot ->
      val sourceValue = snapshot.subjectOrFacetMaybe(source)
      if (sourceValue == null) snapshot
      else setFacetOf(snapshot, target, function.apply(sourceValue.valueString()))
    }
  }
  fun mutateFacet(target: String, function: StringOptionalFunction) =
      setFacetFrom(target, target, function)
  private fun setFacetOf(snapshot: Snapshot, target: String, newValue: String?): Snapshot =
      if (newValue == null) snapshot else snapshot.plusOrReplace(target, SnapshotValue.of(newValue))
  override fun transform(snapshot: Snapshot): Snapshot {
    var current = snapshot
    lenses.forEach { current = it.transform(current) }
    return current
  }
}