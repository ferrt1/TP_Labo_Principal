@echo off
"C:\\Users\\Moras\\android-sdks\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\Moras\\Desktop\\Proyecto profesional\\TP_Labo_Principal\\Cypher_Vault\\desarrollo\\app\\sdk\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=24" ^
  "-DANDROID_PLATFORM=android-24" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\Moras\\android-sdks\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Moras\\android-sdks\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Moras\\android-sdks\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Moras\\android-sdks\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\Moras\\Desktop\\Proyecto profesional\\TP_Labo_Principal\\Cypher_Vault\\desarrollo\\app\\sdk\\build\\intermediates\\cxx\\RelWithDebInfo\\1sf3zd24\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\Moras\\Desktop\\Proyecto profesional\\TP_Labo_Principal\\Cypher_Vault\\desarrollo\\app\\sdk\\build\\intermediates\\cxx\\RelWithDebInfo\\1sf3zd24\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=RelWithDebInfo" ^
  "-BC:\\Users\\Moras\\Desktop\\Proyecto profesional\\TP_Labo_Principal\\Cypher_Vault\\desarrollo\\app\\sdk\\.cxx\\RelWithDebInfo\\1sf3zd24\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
