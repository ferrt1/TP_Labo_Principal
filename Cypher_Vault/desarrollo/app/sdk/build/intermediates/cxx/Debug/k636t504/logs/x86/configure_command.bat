@echo off
"C:\\Users\\l\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\l\\Desktop\\TPFaceAuthn\\Cypher_Vault\\desarrollo\\app\\sdk\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\l\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\l\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\l\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\l\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\l\\Desktop\\TPFaceAuthn\\Cypher_Vault\\desarrollo\\app\\sdk\\build\\intermediates\\cxx\\Debug\\k636t504\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\l\\Desktop\\TPFaceAuthn\\Cypher_Vault\\desarrollo\\app\\sdk\\build\\intermediates\\cxx\\Debug\\k636t504\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\l\\Desktop\\TPFaceAuthn\\Cypher_Vault\\desarrollo\\app\\sdk\\.cxx\\Debug\\k636t504\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
