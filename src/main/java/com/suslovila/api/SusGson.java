//package com.suslovila.api;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonIOException;
//import com.google.gson.JsonSyntaxException;
//import com.google.gson.TypeAdapter;
//import com.google.gson.reflect.TypeToken;
//import com.google.gson.stream.JsonReader;
//
//import java.io.EOFException;
//import java.io.IOException;
//
//public class SusGson {
//    public <T> T fromJson(JsonReader reader, TypeToken<T> typeOfT) throws JsonIOException, JsonSyntaxException {
//        boolean isEmpty = true;
//        boolean oldLenient = reader.isLenient();
//        reader.setLenient(true);
//        try {
//            reader.peek();
//            isEmpty = false;
//            TypeAdapter<T> typeAdapter = getAdapter(typeOfT);
//            return typeAdapter.read(reader);
//        } catch (EOFException e) {
//            /*
//             * For compatibility with JSON 1.5 and earlier, we return null for empty
//             * documents instead of throwing.
//             */
//            if (isEmpty) {
//                return null;
//            }
//            throw new JsonSyntaxException(e);
//        } catch (IllegalStateException e) {
//            throw new JsonSyntaxException(e);
//        } catch (IOException e) {
//            // TODO(inder): Figure out whether it is indeed right to rethrow this as JsonSyntaxException
//            throw new JsonSyntaxException(e);
//        } catch (AssertionError e) {
//            throw new AssertionError("AssertionError (GSON ): " + e.getMessage(), e);
//        } finally {
//            reader.setLenient(oldLenient);
//        }
//    }
//}
