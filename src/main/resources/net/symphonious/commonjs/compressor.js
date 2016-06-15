function compressScript(script, originalSourceMap) {
    var ast = UglifyJS.parse(script);
    ast.figure_out_scope();
    var compressor = UglifyJS.Compressor();
    ast = ast.transform(compressor);
    ast.figure_out_scope();
    ast.compute_char_frequency();
    ast.mangle_names();

    var parsedOriginalSourceMap = JSON.parse(originalSourceMap);
    var sourceMap = UglifyJS.SourceMap({orig: parsedOriginalSourceMap});
    for (var i = 0; i < parsedOriginalSourceMap.sources.length; i++)
    {
        sourceMap.get().setSourceContent(parsedOriginalSourceMap.sources[i], parsedOriginalSourceMap.sourcesContent[i]);
    }
    var stream = UglifyJS.OutputStream({source_map: sourceMap});
    ast.print(stream);

    return {
        script: stream.toString(),
        sourceMap: sourceMap.toString()
    };
}