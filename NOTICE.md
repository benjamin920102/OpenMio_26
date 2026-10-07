# NOTICE — Provenance, License, and Anti-Takedown Statement

## What this is

**OpenMio** is an independent, transformative port of a Minecraft 1.21.1 Fabric
utility mod to **Minecraft 26.2**. The source in this repository was
deobfuscated, decompiled, remapped to Yarn, hand-renamed, and ported file by
file. It contains **ALOT OF BUGS** and is published for research and education.

## License — GPL-3.0-or-later, irrevocable

This program is free software: you can redistribute it and/or modify it under
the terms of the **GNU General Public License as published by the Free Software
Foundation, either version 3 of the License, or (at your option) any later
version**. See `LICENSE` for the full text.

Key points of GPL-3.0 §2 that matter here:

- All rights granted under this License are granted **for the term of
  copyright** and are **irrevocable provided the stated conditions are met**.
- Every recipient who conveys a covered work **automatically receives a license
  from the original licensors** (§10). No single party can retroactively pull
  the rights of downstream users who comply with the license.
- Modified versions must be marked as changed (§5a) — this repo does so: it is
  explicitly a modified, ported version, not the original.

You cannot "un-publish" these freedoms once granted. Any attempt to revoke the
license from compliant users is void under the license terms.

## Why a takedown should not apply

1. **No Mojang / Minecraft assets are distributed here.** No game jars,
   mappings beyond community Yarn, textures, or proprietary binaries are
   included. The build downloads Minecraft via Fabric Loom at build time.
2. **No obfuscated original is redistributed.** `libs/mio-loader.jar` is
   **not** included in this repository. The code here is rewritten/ported
   source, not a byte copy of any prior release.
3. **Transformative, interoperable, research use.** Interoperability code,
   mixin shims, and compatibility renames are functional adaptations to a new
   game version (26.2), with extensive original authorship in the port.
4. **Attribution, not impersonation.** This project does not claim to be the
   original client and does not use its branding to mislead. "OpenMio" is a
   community port name.

## If you believe you hold rights in prior code

- Open an issue or contact the maintainer before filing a platform takedown.
- Identify the **specific files and lines** you claim, with proof of ownership
  and registration where applicable.
- Overbroad or bad-faith takedown notices are actionable under
  **17 U.S.C. § 512(f)**. This project keeps full git history precisely so
  authorship and provenance can be audited.

## Trademarks

"Minecraft" and "Mojang" belong to their respective owners. This project is not
affiliated with or endorsed by Mojang, Microsoft, Fabric, Baritone, or any
prior client author.
