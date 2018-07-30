# Copyright (C) 2018 VNC Automotive Ltd.  All Rights Reserved.

# The license(s) for the AR6003 firmware binaries.
LICENSE = "CLOSED"

# Ensure that linux-firmware gets installed first (as we may need to remove
# certain firmware binaries that are known to conflict).
DEPENDS = "linux-firmware"

# The AR6003 firmware binaries, by hardware revision. You must obtain these
# firmware binaries yourself, and place them in a subdirectory named
# vncautomotive-ar6003-firmware, at the same level as this recipe. You may need to
# amend the hardware revision for your platform. Any changes here must also be
# reflected in do_install().
SRC_URI = "file://hw2.1.1"

# Include all firmware binaries in the package.
FILES_${PN} = "/"

# The location of the firmware binaries in the build directory.
S = "${WORKDIR}"

do_install() {
  # Install the hw2.1.1 firmware binaries.
  install -d ${D}/lib/firmware/ath6k/AR6003/hw2.1.1/
  install -m 0644 ${S}/hw2.1.1/* ${D}/lib/firmware/ath6k/AR6003/hw2.1.1/
}

pkg_postinst_vncautomotive-ar6003-firmware() {
  # Remove ath6k hw2.1.1 firmware binaries that are known to conflict.
  # If we're running on-device, then $D is empty-string.
  rm -f $D/lib/firmware/ath6k/AR6003/hw2.1.1/athwlan.bin
  rm -f $D/lib/firmware/ath6k/AR6003/hw2.1.1/bdata.SD31.bin
  rm -f $D/lib/firmware/ath6k/AR6003/hw2.1.1/bdata.SD32.bin
  rm -f $D/lib/firmware/ath6k/AR6003/hw2.1.1/bdata.WB31.bin
  rm -f $D/lib/firmware/ath6k/AR6003/hw2.1.1/data.patch.bin
  rm -f $D/lib/firmware/ath6k/AR6003/hw2.1.1/endpointping.bin
  rm -f $D/lib/firmware/ath6k/AR6003/hw2.1.1/fw-2.bin
  rm -f $D/lib/firmware/ath6k/AR6003/hw2.1.1/fw-3.bin
  rm -f $D/lib/firmware/ath6k/AR6003/hw2.1.1/otp.bin
}
