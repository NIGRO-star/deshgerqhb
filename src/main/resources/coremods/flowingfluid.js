// Patches FlowingFluid.canHoldFluid (SRG: func_211761_a, 1.16.5) so that, at the very start,
// it asks WaterProtection.blocksWater(state, fluid) and returns false if the block is protected.
//
// Equivalent Java inserted at the top of the method:
//     if (com.infestationpotion.WaterProtection.blocksWater(state, fluidIn)) return false;
//
// Local variables of the instance method: 0 = this, 1 = world, 2 = pos, 3 = state, 4 = fluid.

var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

function initializeCoreMod() {
    return {
        'infestationpotion_can_hold_fluid': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.fluid.FlowingFluid',
                'methodName': 'func_211761_a',
                'methodDesc': '(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/Fluid;)Z'
            },
            'transformer': function (method) {
                var skip = new LabelNode();
                var list = new InsnList();
                list.add(new VarInsnNode(Opcodes.ALOAD, 3));
                list.add(new VarInsnNode(Opcodes.ALOAD, 4));
                list.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    'com/infestationpotion/WaterProtection',
                    'blocksWater',
                    '(Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/Fluid;)Z',
                    false));
                list.add(new JumpInsnNode(Opcodes.IFEQ, skip));
                list.add(new InsnNode(Opcodes.ICONST_0));
                list.add(new InsnNode(Opcodes.IRETURN));
                list.add(skip);
                method.instructions.insert(list);
                return method;
            }
        }
    };
}
