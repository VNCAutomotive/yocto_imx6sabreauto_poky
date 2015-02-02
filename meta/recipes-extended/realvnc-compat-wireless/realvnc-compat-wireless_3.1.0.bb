# Builds and integrates selected compat-wireless kernel modules into an image.
# Copyright (C) 2015 RealVNC Ltd.
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

# The license for compat-wireless.
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/compat-wireless/COPYRIGHT;md5=d7810fab7487fb0aad327b76f1be7cd7"

# This requires the kernel to have been built first, so the kernel tree can be
# used to build the additional modules. The modutils init scripts are also
# required, to load these modules on boot.
DEPENDS = "virtual/kernel modutils-initscripts depmodwrapper-cross"

# Extract the kernel version previously recorded by kernel.bbclass.
KERNEL_VERSION = "${@base_read_file('${STAGING_KERNEL_DIR}/kernel-abiversion')}"

# The compat-wireless source tree. You must copy the compat-wireless source
# directory to a subdirectory named realvnc-compat-wireless, at the same level
# as this recipe.
SRC_URI = "file://compat-wireless"

# The modules provided by this package. For now, only the ath6kl_sdio,
# ath6kl_usb, mac80211 and cfg80211 modules are provided.
FILES_${PN} = "/lib/modules/${KERNEL_VERSION}/updates/drivers/net/wireless/ath/ath6kl/ath6kl_sdio.ko \
               /lib/modules/${KERNEL_VERSION}/updates/drivers/net/wireless/ath/ath6kl/ath6kl_usb.ko \
               /lib/modules/${KERNEL_VERSION}/updates/net/mac80211/mac80211.ko \
               /lib/modules/${KERNEL_VERSION}/updates/net/wireless/cfg80211.ko"

# The location of the compat-wireless source tree in the build directory.
S = "${WORKDIR}/compat-wireless"

do_compile() {
  cd ${S}

  # Build compat-wireless (the source tree already contains a suitable configuration).
  make ARCH=${TARGET_ARCH} CROSS_COMPILE=${TARGET_PREFIX} KLIB=${STAGING_KERNEL_DIR} KLIB_BUILD=${STAGING_KERNEL_DIR} COMPAT_CURDIR=${S}

  # Copy the modules to artifacts/ for subsequent installation.
  for i in `find . -name "*.ko"`; do mkdir -p artifacts/`dirname $i`; cp -Lf $i artifacts/$i; done
}

do_install() {
  # Install the ath6kl_sdio and ath6kl_usb compat-wireless kernel module.
  install -d ${D}/lib/modules/${KERNEL_VERSION}/updates/drivers/net/wireless/ath/ath6kl/
  install -m 0644 ${S}/artifacts/drivers/net/wireless/ath/ath6kl/ath6kl_sdio.ko ${D}/lib/modules/${KERNEL_VERSION}/updates/drivers/net/wireless/ath/ath6kl/
  install -m 0644 ${S}/artifacts/drivers/net/wireless/ath/ath6kl/ath6kl_usb.ko ${D}/lib/modules/${KERNEL_VERSION}/updates/drivers/net/wireless/ath/ath6kl/

  # Install the mac80211 compat-wireless kernel module.
  install -d ${D}/lib/modules/${KERNEL_VERSION}/updates/net/mac80211/
  install -m 0644 ${S}/artifacts/net/mac80211/mac80211.ko ${D}/lib/modules/${KERNEL_VERSION}/updates/net/mac80211/

  # Install the cfg80211 compat-wireless kernel module.
  install -d ${D}/lib/modules/${KERNEL_VERSION}/updates/net/wireless/
  install -m 0644 ${S}/artifacts/net/wireless/cfg80211.ko ${D}/lib/modules/${KERNEL_VERSION}/updates/net/wireless/
}

pkg_postinst_realvnc-compat-wireless() {
  # Create, or append to, /etc/modules to ensure that ath6kl_sdio is loaded on boot.
  # If we're running on-device, then $D is empty-string.
  mkdir -p $D/etc
  cat >> $D/etc/modules << EOF
# Load ath6kl_sdio with WiFi P2P enabled.
ath6kl_sdio ar6k_clock=26000000 ath6kl_p2p=1 wow_mode=2
EOF
  chmod 0644 $D/etc/modules

  if [ -n "$D" ]
  then
    # The rootfs is being created: try to run cross-compiled depmod.
    depmodwrapper -a -b $D ${KERNEL_VERSION}
  else
    # Running on-device: try to run normal depmod
    depmod -a ${KERNEL_VERSION}
  fi
}
